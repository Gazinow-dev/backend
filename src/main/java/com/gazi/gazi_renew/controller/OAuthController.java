package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.domain.AppleLoginParams;
import com.gazi.gazi_renew.domain.GoogleLoginParams;
//import com.gazi.gazi_renew.domain.KakaoLoginParams;
import com.gazi.gazi_renew.domain.NaverLoginParams;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.GoogleApiClient;
import com.gazi.gazi_renew.service.NaverApiClient;
import com.gazi.gazi_renew.service.OAuthLoginService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

//    @GetMapping("/kakao")
//    public ResponseEntity<Response.Body> kakaoCalllback(@RequestParam String code) {
//        KakaoLoginParams kakaoLoginParams = new KakaoLoginParams(code);
//        return oAuthLoginService.login(kakaoLoginParams);
//    }

    @GetMapping("/naver")
    public ResponseEntity<Response.Body> naverCallback(@RequestParam String code, @RequestParam String state) {
        NaverLoginParams naverLoginParams = new NaverLoginParams(code, state);
        return oAuthLoginService.login(naverLoginParams);
    }
    @GetMapping("/google")
    public ResponseEntity<Response.Body> googleCallback(@RequestParam String code) {
        GoogleLoginParams googleLoginParams = new GoogleLoginParams(code);
        return oAuthLoginService.login(googleLoginParams);
    }
    @GetMapping("/apple")
    public ResponseEntity<Response.Body> appleCallback(@RequestParam String code) {
        AppleLoginParams appleLoginParams = new AppleLoginParams(code);
        return oAuthLoginService.login(appleLoginParams);
    }
}
