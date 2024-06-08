package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.enums.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
}
