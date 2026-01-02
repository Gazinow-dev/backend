package com.gazi.gazi_renew.oauth.service;

import com.gazi.gazi_renew.admin.service.SignUpDiscordNotifier;
import com.gazi.gazi_renew.common.config.AppleProperties;
import com.gazi.gazi_renew.common.security.JwtTokenProvider;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.oauth.controller.response.OAuthInfoResponse;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.oauth.domain.OAuthLoginParams;
import com.gazi.gazi_renew.member.controller.port.MemberService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;
    private final RedisUtilService redisUtilService;

    private final AppleProperties appleProperties;
    private final PasswordEncoder passwordEncoder;
    private final SignUpDiscordNotifier signUpDiscordNotifier;

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
                .queryParam("response_type", "code")
                .queryParam("response_mode", "form_post")
                .build().toUri();
    }
    public ResponseEntity<Void> login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Member member = findOrCreateMember(oAuthInfoResponse);

        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), null, authorities);

        ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);
        responseToken = responseToken.login(member.getEmail(), member.getNickName(), member.getSocialLoginIsNewMember());
        //redis에 refresh token 저장
        redisUtilService.setRefreshToken(authentication.getName(), responseToken.getRefreshToken(),
                responseToken.getRefreshTokenExpirationTime());

        return createRedirectResponse(responseToken);
    }
    private Member findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .orElseGet(() -> {
                    try {
                        return newMember(oAuthInfoResponse);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    private Member newMember(OAuthInfoResponse oAuthInfoResponse) throws Exception {
        Member member = Member.saveSocialLoginMember(oAuthInfoResponse, passwordEncoder);
        memberService.validateEmail(member.getEmail());
        memberService.validateNickName(member.getNickName());
        memberRepository.save(member);

        signUpDiscordNotifier.sendSignUpNotification(member, (int) memberRepository.count());
        return member;
    }
    // URI 생성과 리다이렉트 응답을 담당하는 메서드
    private ResponseEntity<Void> createRedirectResponse(ResponseToken responseToken) {
        URI redirectUri = UriComponentsBuilder.fromUriString("gazinow://main")
                .queryParam("accessToken", responseToken.getAccessToken())
                .queryParam("refreshToken", responseToken.getRefreshToken())
                .queryParam("email", responseToken.getEmail())
                .queryParam("nickName", responseToken.getNickName())
                .queryParam("socialLoginIsNewMember", responseToken.isSocialLoginIsNewMember())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
