package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.JwtTokenProvider;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.dto.MemberRequest;
import com.gazi.gazi_renew.dto.MemberResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.ResponseToken;
import com.gazi.gazi_renew.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final Response response;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManagerBuilder managerBuilder;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<Response.Body> signUp(MemberRequest.SignUp signUpDto) {
        Member member = signUpDto.toMember(passwordEncoder);
        try {
            validateEmail(member.getEmail());
            validateNickName(member.getNickName());
            memberRepository.save(member);
            MemberResponse.SignUp requestDto = new MemberResponse.SignUp(member);
            return response.success(requestDto, "회원가입이 완료되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            return response.fail(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    public void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }

    public void validateNickName(String nickName) {
        if (memberRepository.existsByNickName(nickName)) {
            throw new IllegalStateException("중복된 닉네임입니다.");
        }
    }

    @Override
    public ResponseEntity<Response.Body> login(MemberRequest.Login loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 회원의 아이디입니다.")
        );
        // loginDto email, password 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.usernamePasswordAuthenticationToken();
        try {
            // 실제 검증 (사용자 비밀번호 체크)
            // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
            Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken); // 인증 정보를 기반으로 JWT 토큰 생성
            ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);
            responseToken.setMemberId(member.getId());
            responseToken.setNickName(member.getNickName());
            responseToken.setEmail(member.getEmail());

            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), responseToken.getRefreshToken(),
                            responseToken.getRefreshTokenExpirationTime(),
                            TimeUnit.MILLISECONDS
                    );
            return response.success(responseToken, "로그인에 성공했습니다.", HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return response.fail("비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

    }

    @Transactional
    @Override
    public ResponseEntity<Response.Body> logout(MemberRequest.Logout logoutDto) {
        // Access Token 검증
        if (!jwtTokenProvider.validateToken(logoutDto.getAccessToken())) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        // Access Token 에서 Member email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(logoutDto.getAccessToken());


        // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("AT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("AT:" + authentication.getName());
        }
        // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(logoutDto.getAccessToken());
        redisTemplate.opsForValue()
                .set(logoutDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        // firebaseToken 삭제
        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("회원을 찾을 수 없습니다.")
        );
        memberRepository.save(member);

        return response.success("로그아웃 되었습니다.");
    }

    @Override
    public ResponseEntity<Response.Body> reissue(MemberRequest.Reissue reissueDto) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(reissueDto.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }


        // Access Token 에서 Member email 가져옴.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissueDto.getAccessToken());

        log.info("유저 email: " + authentication.getName());

        log.info("엑세스 토큰 만료까지 남은시간(ms) : " + jwtTokenProvider.getExpiration(reissueDto.getAccessToken()));
        log.info("리프레쉬 토큰 만료까지 남은시간(ms): " + jwtTokenProvider.getExpiration(reissueDto.getRefreshToken()));

        // Redis 에서 Member email 을 기반으로 저장된 Refresh Token 을 가져옴.
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        log.info("Redis에서 찾은 refreshToken :" + refreshToken);
        log.info("리프레쉬 토큰이 존재하는지:" + redisTemplate.hasKey("RT:" + authentication.getName()));

        // 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if (ObjectUtils.isEmpty(refreshToken)) {
            log.info("Redis 에 RefreshToken 이 존재하지 않는 경우 처리");
            log.info("accessToken: " + reissueDto.getAccessToken());
            log.info("refreshToken: " + reissueDto.getRefreshToken());
            jwtTokenProvider.validateToken(refreshToken);
            return response.fail("잘못된 요청입니다. 엑세스토큰으로 찾은 유저 이메일 : " + authentication.getName(), HttpStatus.BAD_REQUEST);
        }
        if (!refreshToken.equals(reissueDto.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("회원을 찾을 수 없습니다.")
        );

        // 새로운 토큰 생성
        ResponseToken tokenInfo = jwtTokenProvider.generateToken(authentication);
        tokenInfo.setMemberId(member.getId());
        tokenInfo.setEmail(member.getEmail());
        tokenInfo.setNickName(member.getNickName());


        Date date = new Date(tokenInfo.getRefreshTokenExpirationTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("재발급으로 발급된 토큰에 만료날짜:" + formatter.format(date));

        // RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }
}
