package com.gazi.gazi_renew.oauth.domain;

import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
public class GoogleLoginParams implements OAuthLoginParams {
    private final String authorizationCode;
    public GoogleLoginParams(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
