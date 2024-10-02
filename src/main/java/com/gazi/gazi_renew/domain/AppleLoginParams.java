package com.gazi.gazi_renew.domain;

import com.gazi.gazi_renew.domain.enums.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
public class AppleLoginParams implements OAuthLoginParams{
    private final String authorizationCode;
    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.APPLE;
    }
    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
