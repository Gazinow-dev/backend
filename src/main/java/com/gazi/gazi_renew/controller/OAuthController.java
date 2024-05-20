package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.domain.GoogleLoginParams;
import com.gazi.gazi_renew.domain.KakaoLoginParams;
import com.gazi.gazi_renew.domain.NaverLoginParams;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.GoogleApiClient;
import com.gazi.gazi_renew.service.NaverApiClient;
import com.gazi.gazi_renew.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;


@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
@RestController
public class OAuthController extends BaseController{
    private final OAuthLoginService oAuthLoginService;
    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;

    @GetMapping("/kakao")
    public ResponseEntity<Response.Body> kakaoCalllback(@RequestParam String code) {
        KakaoLoginParams kakaoLoginParams = new KakaoLoginParams(code);
        return oAuthLoginService.login(kakaoLoginParams);
    }


    @GetMapping("/naver")
    public ResponseEntity<Response.Body> naverCalllback(@RequestParam String code, @RequestParam String state) {
        NaverLoginParams naverLoginParams = new NaverLoginParams(code, state);
        return oAuthLoginService.login(naverLoginParams);
    }

    @GetMapping("/google")
    public ResponseEntity<Response.Body> googleCalllback(@RequestParam String code) {
        GoogleLoginParams googleLoginParams = new GoogleLoginParams(code);
        return oAuthLoginService.login(googleLoginParams);
    }

    @GetMapping("/login")
    public ResponseEntity<String> redirectToOAuth(String socialLoginType) {
        if (socialLoginType == null || socialLoginType.isEmpty()) {
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"error\": \"소셜 로그인 타입이 입력되지 않았습니다\"}");
        }
        switch (socialLoginType) {
            case "google": {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                        .queryParam("client_id", googleApiClient.getClientId())
                        .queryParam("redirect_uri", googleApiClient.getRedirectUrl())
                        .queryParam("response_type", "code")
                        .queryParam("scope", "email profile");

                URI googleAuthUri = uriBuilder.build().toUri();

                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(googleAuthUri);

                return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
            }
            case "naver": {
                String randomState = UUID.randomUUID().toString();

                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                        .queryParam("client_id", naverApiClient.getClientId())
                        .queryParam("redirect_uri", naverApiClient.getRedirectUrl())
                        .queryParam("response_type", "code")
                        .queryParam("state", randomState);

                URI naverAuthUri = uriBuilder.build().toUri();

                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(naverAuthUri);

                return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
            }
            default: {
                return ResponseEntity.badRequest()
                        .header("Content-Type", "application/json")
                        .body("{\"error\": \"유효하지 않은 소셜 로그인 타입입니다\"}");
            }
        }

    }
}
