package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.JwtTokenProvider;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.enums.Role;
import com.gazi.gazi_renew.dto.OAuthInfoResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.ResponseToken;
import com.gazi.gazi_renew.domain.OAuthLoginParams;
import com.gazi.gazi_renew.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final Response response;

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    private final PasswordEncoder passwordEncoder;


    public ResponseEntity<Response.Body> login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        String email = findOrCreateMember(oAuthInfoResponse);
        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);
        responseToken.setEmail(email);

        responseToken.setNickName(oAuthInfoResponse.getNickname());
        return response.success(responseToken, "로그인에 성공했습니다.", HttpStatus.OK);
    }
    private String findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getEmail)
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }
    private String newMember(OAuthInfoResponse oAuthInfoResponse) {
        String email = oAuthInfoResponse.getEmail();
        String nickname = oAuthInfoResponse.getNickname();
        // 소셜로그인으로 회원 가입 시 nickname이 null일 경우 임의로 메일의 id로 대체
        if (nickname == null || nickname.isEmpty()) {
            nickname = email.substring(0, email.indexOf("@"));
        }
        Member member = Member.builder()
                .isAgree(true)
                .email(email)
                .password(passwordEncoder.encode("dummy"))
                .role(Role.valueOf("ROLE_USER"))
                .nickName(nickname)
                .provider(oAuthInfoResponse.getOAuthProvider())
                .build();

        memberService.validateEmail(member.getEmail());
        memberService.validateNickName(member.getNickName());
        memberRepository.save(member);

        return member.getEmail();
    }
}
