package com.gazi.gazi_renew.oauth.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleInfoResponse implements OAuthInfoResponse {
    @JsonProperty("email")
    private String email;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sub")
    private String sub;

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
    public String getSub() {
        return sub;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
}
