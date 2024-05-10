package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.domain.GoogleLoginParams;
import com.gazi.gazi_renew.domain.KakaoLoginParams;
import com.gazi.gazi_renew.domain.NaverLoginParams;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
@RestController
public class OAuthController extends BaseController{
    private final OAuthLoginService oAuthLoginService;

    @GetMapping("/kakao")
    public ResponseEntity<Response.Body>  kakaoCalllback(@RequestParam String code) {
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
}
