package com.gazi.gazi_renew.oauth.controller.response;

import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
}
