package com.gazi.gazi_renew.oauth.controller.port;

import com.gazi.gazi_renew.oauth.domain.OAuthLoginParams;
import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.oauth.controller.response.OAuthInfoResponse;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
