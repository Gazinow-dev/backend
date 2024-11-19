package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.common.config.JwtTokenProvider;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
public class FakeJwtTokenProvider extends JwtTokenProvider {


    public FakeJwtTokenProvider(String secretKey) {
        super(secretKey);
    }

    public ResponseToken generateToken(Authentication authentication) {
        // 임의의 테스트용 토큰과 만료 시간 반환
        return ResponseToken.builder()
                .grantType("Bearer")
                .accessToken("fakeAccessToken")
                .refreshToken("fakeRefreshToken")
                .accessTokenExpirationTime(new Date().getTime() + 3600 * 1000L) // 1시간 후 만료
                .refreshTokenExpirationTime(new Date().getTime() + 30 * 24 * 3600 * 1000L) // 30일 후 만료
                .build();
    }

    public ResponseToken generateMemberAndToken(Authentication authentication, Member member) {
        // Member 정보와 함께 임의의 테스트용 토큰 반환
        return ResponseToken.builder()
                .grantType("Bearer")
                .accessToken("fakeAccessToken")
                .refreshToken("mw310@naver.com")
                .accessTokenExpirationTime(new Date().getTime() + 3600 * 1000L)
                .refreshTokenExpirationTime(new Date().getTime() + 30 * 24 * 3600 * 1000L)
                .memberId(member.getId())
                .nickName(member.getNickName())
                .email(member.getEmail())
                .firebaseToken(member.getFirebaseToken())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // Fake Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken("mw310@naver.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public boolean validateToken(String token) {
        // 가짜 토큰이 만료되거나 잘못된 토큰인지 간단히 검사
        return !"expiredToken".equals(token);
    }

    public Long getExpiration(String accessToken) {
        // 테스트를 위해 임의의 만료 시간 반환 (예: 1시간 후 만료)
        return 3600 * 1000L;
    }
}
