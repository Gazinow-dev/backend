package com.gazi.gazi_renew.domain;

import com.gazi.gazi_renew.domain.enums.OAuthProvider;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
public class KakaoLoginParams implements OAuthLoginParams {
    private String authorizationCode;
    public KakaoLoginParams(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
