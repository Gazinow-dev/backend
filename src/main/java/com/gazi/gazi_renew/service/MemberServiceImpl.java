package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.JwtTokenProvider;
import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.dto.MemberRequest;
import com.gazi.gazi_renew.dto.MemberResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.ResponseToken;
import com.gazi.gazi_renew.repository.MemberRepository;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.text.SimpleDateFormat;
import java.util.*;
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
    private final JavaMailSender emailSender;
    private final RedisUtilService redisUtilService;

    @Override
    public ResponseEntity<Response.Body> signUp(@Valid MemberRequest.SignUp signUpDto, Errors errors) {
        Member member = signUpDto.toMember(passwordEncoder);
        try {
            if (errors.hasErrors()) {
                return validateHandling(errors);
            }
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
    public ResponseEntity<Response.Body> login(@Valid MemberRequest.Login loginDto) {
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


        // Redis 에서 해당 Member email 로 저장된 Access Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
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

    @Override
    public ResponseEntity<Response.Body> changeNickName(@Valid MemberRequest.NickName nickNameDto, Errors errors) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            if (memberRepository.existsByNickName(nickNameDto.getNickName())) {
                throw new IllegalStateException("중복된 닉네임입니다.");
            } else {
                member.setNickName(nickNameDto.getNickName());
                memberRepository.save(member);
                MemberResponse.NickName nickName = new MemberResponse.NickName();
                nickName.setNickName(member.getNickName());
                return response.success(nickName, "닉네임 변경 완료",HttpStatus.OK);
            }

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return response.fail(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Override
    public ResponseEntity<Response.Body> checkPassword(MemberRequest.CheckPassword checkPassword) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            if (passwordEncoder.matches(checkPassword.getCheckPassword(), member.getPassword())) {
                return response.success("비밀번호가 일치합니다.");
            } else {
                return response.fail("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<Response.Body> findPassword(MemberRequest.IsUser isUserRequest){
        try{
            String password = "";

            MemberResponse.isUser isUser = new MemberResponse.isUser();

            Optional<Member> member =memberRepository.findByEmailAndNickName(isUserRequest.getEmail(),isUserRequest.getNickname());
            if(member.isPresent()) {
                isUser.setIsUser(true);
                // 비밀번호 발급
                password = getTempPassword();
                // 비밀번호 수정
                member.get().setPassword(passwordEncoder.encode(password));
                memberRepository.save(member.get());
                // 이메일로 임시비밀번호 전송
                MimeMessage message = createMessageToPassword(isUserRequest.getEmail(), password);
                try{//예외처리
                    emailSender.send(message);
                }catch(MailException es){
                    es.printStackTrace();
                    throw new IllegalArgumentException();
                }
            }
            return response.success("임시비밀번호 발급: " +password);
        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }


    //랜덤함수로 임시비밀번호 구문 만들기
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        char[] specialSet = new char[] {'!', '#', '$', '%', '&','~'};


        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }

        for (int i = 0; i < 2 ; i++) {
            idx = (int) (specialSet.length * Math.random());
            str += specialSet[idx];
        }
        return str;
    }



    @Override
    public ResponseEntity<Response.Body> changePassword(@Valid MemberRequest.Password passwordDto, Errors errors) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            MemberRequest.CheckPassword checkPassword = new MemberRequest.CheckPassword();
            checkPassword.setCheckPassword(passwordDto.getCurPassword());

            if (checkPassword(checkPassword).getStatusCode().equals(HttpStatus.OK)) {
                if (passwordDto.getChangePassword().equals(passwordDto.getConfirmPassword())) {
                    member.setPassword(passwordEncoder.encode(passwordDto.getChangePassword()));
                    memberRepository.save(member);
                    return response.success("비밀번호 변경을 완료했습니다.");
                } else {
                    return response.fail("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return response.fail("현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Response.Body> deleteMember(MemberRequest.DeleteMember deleteMemberDto) {
        try{
            String email = SecurityUtil.getCurrentUserEmail();
            Member member = memberRepository.getReferenceByEmail(email).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            memberRepository.delete(member);

            // Redis 에서 해당 Member email 로 저장된 Access Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            if (redisTemplate.opsForValue().get("AT:" + email) != null) {
                // Refresh Token 삭제
                redisTemplate.delete("AT:" + email);
            }
            // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            if (redisTemplate.opsForValue().get("RT:" + email) != null) {
                // Refresh Token 삭제
                redisTemplate.delete("RT:" + email);
            }
            return response.success("회원 탈퇴 완료.");

        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    /* 회원가입 시, 유효성 체크 */
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Response.Body> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        // 유효성 검사에 실패한 필드 목록을 받음
        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
    }

    private MimeMessage createMessageToPassword(String to, String password)throws Exception{
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("가는길 지금 이메일 인증");//제목

        String msgg="";
        msgg+= "<div style='margin:20px; color=#000000'>";
        msgg+= "<h1> 임시 비밀번호 발급 </h1>";
        msgg+= "<p> 안녕하세요 가는길지금 Gazi 입니다.";
        msgg+= "<br>";
        msgg+= "아래 임시비밀번호를 통해 로그인 해주세요.</p>";
        msgg+= "비밀번호 변경을 통해 원하시는 비밀번호로 변경 바랍니다.";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid white; font-family:verdana; background-color: #F2EBFF';>";
        msgg+= "<div style='font-size:300%; color: #8446E7'>";
        msgg+= password;
        msgg+= "</div>";
        msgg+= "</div>";
        msgg+= "<hr>";
        msgg+= "<p><span style='color:#323232; font-weight:bold'>가는길지금에 가입하신 적이 없다면, 이 메일을 무시하세요.</span> <br> ";
        msgg+= "<span style='color:#9D9D9D'>본 메일은 발신 전용으로 문의에 대한 회신이 되지 않습니다. 궁금한 사항은 gazinowcs@gmail.com로 문의 부탁드립니다.</span></p>";


        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("gazinowcs@gmail.com","gazi"));//보내는 사람

        return message;
    }

    private MimeMessage createMessage(String to, String keyValue)throws Exception{
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("가는길 지금 이메일 인증");//제목

        String msgg="";
        msgg+= "<div style='margin:20px; color=#000000'>";
        msgg+= "<h1> 이메일 인증 </h1>";
        msgg+= "<p> 안녕하세요 가는길지금 Gazi 입니다.";
        msgg+= "<br>";
        msgg+= "아래 번호를 인증번호 입력란에 입력 후 회원가입을 완료해주세요.</p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid white; font-family:verdana; background-color: #F2EBFF';>";
        msgg+= "<div style='font-size:300%; color: #8446E7'>";
        msgg+= keyValue;
        msgg+= "</div>";
        msgg+= "</div>";
        msgg+= "<hr>";
        msgg+= "<p><span style='color:#323232; font-weight:bold'>가는길지금에 가입하신 적이 없다면, 이 메일을 무시하세요.</span> <br> ";
        msgg+= "<span style='color:#9D9D9D'>본 메일은 발신 전용으로 문의에 대한 회신이 되지 않습니다. 궁금한 사항은 gazinowcs@gmail.com로 문의 부탁드립니다.</span></p>";


        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("gazinowcs@gmail.com","gazi"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) { // 인증코드 8자리
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }

    @Override
    public ResponseEntity<Response.Body> sendSimpleMessage(String to) throws Exception {
        Response.Body chekEmailBody = checkEmail(to).getBody();
        if(chekEmailBody.getResult().equals("fail")){
            return response.fail(chekEmailBody.getMessage(), HttpStatus.CONFLICT);
        }
        String keyValue = createKey();
        MimeMessage message = createMessage(to,keyValue);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        if(redisUtilService.getData(to) == null){
            //유효시간 5분
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }else{
            redisTemplate.delete(to);
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }

        return response.success(keyValue,"인증번호를 발송하였습니다.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> checkEmail(String email) {
        try {
            validateEmail(email);
            return response.success("회원가입이 가능한 이메일입니다.");
        } catch (IllegalStateException e) {
            return response.fail("이미 가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Response.Body> checkNickName(String nickName) {
        if(memberRepository.existsByNickName(nickName)) {
            return response.fail("중복된 닉네임입니다.", HttpStatus.CONFLICT);
        } else{
            return response.success(nickName,"사용가능한 닉네임입니다.", HttpStatus.OK);
        }

    }
}
