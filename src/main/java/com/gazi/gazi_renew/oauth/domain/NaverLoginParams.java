package com.gazi.gazi_renew.oauth.domain;

import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
public class NaverLoginParams implements OAuthLoginParams {
    private final String authorizationCode;
    private final String state;
    public NaverLoginParams(String authorizationCode, String state) {
        this.authorizationCode = authorizationCode;
        this.state = state;
    }
    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);
        return body;
    }
}
