package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.config.AppleProperties;
import com.gazi.gazi_renew.domain.AppleTokens;
import com.gazi.gazi_renew.domain.OAuthLoginParams;
import com.gazi.gazi_renew.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.dto.AppleInfoResponse;
import com.gazi.gazi_renew.dto.OAuthInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleApiClient implements OAuthApiClient {
    private static final String GRANT_TYPE = "authorization_code";

    private final AppleProperties appleProperties;
    private final AppleLoginUtil appleLoginUtilService;
    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.APPLE;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = appleProperties.getAuth() + "/auth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();

        String clientSecret = appleLoginUtilService.generateClientSecret();

        body.add("client_id", appleProperties.getClientId());
        body.add("redirect_url", appleProperties.getRedirectUrl());
        body.add("client_secret", clientSecret);
        body.add("grant_type", GRANT_TYPE);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        AppleTokens response = restTemplate.postForObject(url, request, AppleTokens.class);

        log.info("Apple 인증 서버 응답 :" + response);

        return response.getIdToken();
    }
    @Override
    public OAuthInfoResponse requestOauthInfo(String idToken) {
        AppleInfoResponse appleInfoResponse = decodePayload(idToken, AppleInfoResponse.class);

        // email과 nickname이 null 또는 빈 값일 경우, sub 값을 할당
        if (appleInfoResponse.getEmail() == null || appleInfoResponse.getEmail().isEmpty()) {
            appleInfoResponse.setEmail(appleInfoResponse.getSub());
        }
        if (appleInfoResponse.getNickname() == null || appleInfoResponse.getNickname().isEmpty()) {
            appleInfoResponse.setName(appleInfoResponse.getSub());
        }
        return appleInfoResponse;
    }
    private static <T> T decodePayload(String token, Class<T> targetClass) {

        String[] tokenParts = token.split("\\.");
        String payloadJWT = tokenParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.info("Apple 로그인 응답: " + payload);
        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }

}