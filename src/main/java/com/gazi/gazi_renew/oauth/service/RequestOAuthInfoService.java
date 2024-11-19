package com.gazi.gazi_renew.oauth.service;

import com.gazi.gazi_renew.oauth.controller.port.OAuthApiClient;
import com.gazi.gazi_renew.oauth.domain.OAuthLoginParams;
import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.oauth.controller.response.OAuthInfoResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(OAuthLoginParams params) {
        OAuthApiClient client = clients.get(params.oAuthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestOauthInfo(accessToken);
    }
}
