package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.AppleProperties;
import com.gazi.gazi_renew.config.JwtTokenProvider;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.enums.Role;
import com.gazi.gazi_renew.dto.OAuthInfoResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.ResponseToken;
import com.gazi.gazi_renew.domain.OAuthLoginParams;
import com.gazi.gazi_renew.repository.MemberRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final Response response;

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;
    private final AppleApiClient appleApiClient;

    private final AppleProperties appleProperties;
    private final PasswordEncoder passwordEncoder;
    /**
     * socialLogin 타입에 따라 분기 처리
     * @param : String socialLoginType
     * return URI
     */
    public URI getOAuthRedirectUri(String socialLoginType) {
        switch (socialLoginType) {
            case "google":
                return getGoogleAuthUri();
            case "naver":
                return getNaverAuthUri();
            case "apple":
                return getAppleAuthUri();
            default:
                throw new IllegalArgumentException("유효하지 않은 소셜 로그인 타입입니다.");
        }
    }
    /**
     * 구글 인가 코드 받아오기
     */
    private URI getGoogleAuthUri() {
        return UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", googleApiClient.getClientId())
                .queryParam("redirect_uri", googleApiClient.getRedirectUrl())
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .build().toUri();
    }
    /**
     * 네이버 인가 코드 받아오기
     */
    private URI getNaverAuthUri() {
        String randomState = UUID.randomUUID().toString();
        return UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("client_id", naverApiClient.getClientId())
                .queryParam("redirect_uri", naverApiClient.getRedirectUrl())
                .queryParam("response_type", "code")
                .queryParam("state", randomState)
                .build().toUri();
    }
    /**
     * 애플 인가 코드 받아오기
     */
    private URI getAppleAuthUri() {
        return UriComponentsBuilder.fromHttpUrl("https://appleid.apple.com/auth/authorize")
                .queryParam("client_id", appleProperties.getClientId())
                .queryParam("redirect_uri", appleProperties.getRedirectUrl())
                .queryParam("scope", "email name")
                .queryParam("response_type", "form_post")
                .build().toUri();
    }
    public ResponseEntity<Void> login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Member member = findOrCreateMember(oAuthInfoResponse);

        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), null, authorities);

        ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);
        responseToken.setEmail(member.getEmail());
        responseToken.setNickName(member.getNickName());
        return createRedirectResponse(responseToken);
    }
    private Member findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }
    private Member newMember(OAuthInfoResponse oAuthInfoResponse) {
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

        return member;
    }
    // URI 생성과 리다이렉트 응답을 담당하는 메서드
    private ResponseEntity<Void> createRedirectResponse(ResponseToken responseToken) {
        URI redirectUri = UriComponentsBuilder.fromUriString("gazinow://main")
                .queryParam("accessToken", responseToken.getAccessToken())
                .queryParam("refreshToken", responseToken.getRefreshToken())
                .queryParam("email", responseToken.getEmail())
                .queryParam("nickName", responseToken.getNickName())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
