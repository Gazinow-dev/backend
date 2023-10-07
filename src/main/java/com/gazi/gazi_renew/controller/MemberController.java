package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.MemberRequest.Login;
import com.gazi.gazi_renew.dto.MemberRequest.Logout;
import com.gazi.gazi_renew.dto.MemberRequest.Reissue;
import com.gazi.gazi_renew.dto.MemberRequest.SignUp;
import com.gazi.gazi_renew.dto.Response.Body;
import com.gazi.gazi_renew.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("signup")
    public ResponseEntity<Body> signup(@RequestBody SignUp signUpDto, Errors errors) {
        return memberService.signUp(signUpDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Body> login(@RequestBody @Valid Login loginDto, Errors errors) {
        return memberService.login(loginDto);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Body> logout(@RequestBody Logout logoutDto) {
        return memberService.logout(logoutDto);
    }

    // 자동 로그인
    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@RequestBody Reissue reissueDto) {
        return memberService.reissue(reissueDto);
    }
}
