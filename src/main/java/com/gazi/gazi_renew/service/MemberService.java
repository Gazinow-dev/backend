package com.gazi.gazi_renew.service;


import com.gazi.gazi_renew.dto.MemberRequest;
import com.gazi.gazi_renew.dto.Response.Body;
import org.springframework.http.ResponseEntity;


public interface MemberService {
    // 회원가입
    ResponseEntity<Body> signUp(MemberRequest.SignUp signUpDto);

    // 로그인
    ResponseEntity<Body> login(MemberRequest.Login loginDto);

    // 로그아웃
    ResponseEntity<Body> logout(MemberRequest.Logout logoutDto);

    // 자동 로그인
    ResponseEntity<Body> reissue(MemberRequest.Reissue reissueDto);
}
