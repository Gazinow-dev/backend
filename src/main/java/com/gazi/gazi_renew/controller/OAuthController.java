package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.domain.AppleLoginParams;
import com.gazi.gazi_renew.domain.GoogleLoginParams;
//import com.gazi.gazi_renew.domain.KakaoLoginParams;
import com.gazi.gazi_renew.domain.NaverLoginParams;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.ResponseToken;
import com.gazi.gazi_renew.service.GoogleApiClient;
import com.gazi.gazi_renew.service.NaverApiClient;
import com.gazi.gazi_renew.service.OAuthLoginService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping("/naver")
    public ResponseEntity<Void> naverCallback(@RequestParam String code, @RequestParam String state) {
        NaverLoginParams naverLoginParams = new NaverLoginParams(code, state);
        return oAuthLoginService.login(naverLoginParams);
    }

    @Hidden
    @GetMapping("/google")
    public ResponseEntity<Void> googleCallback(@RequestParam String code) {
        GoogleLoginParams googleLoginParams = new GoogleLoginParams(code);
        return oAuthLoginService.login(googleLoginParams);
    }

    @Hidden
    @PostMapping("/apple")
    public ResponseEntity<Void> appleCallback(@RequestParam String code) {
        AppleLoginParams appleLoginParams = new AppleLoginParams(code);
        return oAuthLoginService.login(appleLoginParams);
    }

    @Operation(summary = "소셜 로그인")
    @GetMapping("/login")
    public ResponseEntity redirectToOAuth(@RequestParam String socialLoginType) {
        if (socialLoginType == null || socialLoginType.isEmpty()) {
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"error\": \"소셜 로그인 타입이 입력되지 않았습니다\"}");
        }
        try {
            URI redirectUri = oAuthLoginService.getOAuthRedirectUri(socialLoginType);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(redirectUri);
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"error\": \"유효하지 않은 소셜 로그인 타입입니다\"}");
        }
    }
}
