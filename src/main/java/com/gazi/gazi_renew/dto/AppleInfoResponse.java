package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gazi.gazi_renew.domain.enums.OAuthProvider;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleInfoResponse implements OAuthInfoResponse {
    @JsonProperty("email")
    private String email;
    @JsonProperty("name")
    private String name;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return name;
    }
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.APPLE;
    }
}