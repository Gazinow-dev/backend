package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.domain.AppleLoginParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest

class AppleApiClientTest {
    @Autowired
    AppleApiClient appleApiClient;

    @Test
    void getToken() {

        String authorizationCode = "클라이언트로 부터 받은 애플 인가코드";
        AppleLoginParams appleLoginParams = new AppleLoginParams(authorizationCode);
        var source = appleApiClient.requestAccessToken(appleLoginParams);
    }
}