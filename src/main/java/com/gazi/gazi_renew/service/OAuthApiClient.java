package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.domain.OAuthLoginParams;
import com.gazi.gazi_renew.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.dto.OAuthInfoResponse;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
