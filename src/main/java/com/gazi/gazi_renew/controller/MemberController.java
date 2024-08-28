package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.MemberRequest;
import com.gazi.gazi_renew.dto.MemberRequest.Login;
import com.gazi.gazi_renew.dto.MemberRequest.Logout;
import com.gazi.gazi_renew.dto.MemberRequest.Reissue;
import com.gazi.gazi_renew.dto.MemberRequest.SignUp;
import com.gazi.gazi_renew.dto.Response.Body;
import com.gazi.gazi_renew.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController extends BaseController{
    private final MemberService memberService;

    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("signup")
    public ResponseEntity<Body> signup(@RequestBody @Valid SignUp signUpDto, Errors errors) {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        memberService.signUp(signUpDto, errors);
        // 로그인 까지 진행
        Login loginDto = new Login();
        loginDto.setEmail(signUpDto.getEmail());
        loginDto.setPassword(signUpDto.getPassword());
        return memberService.login(loginDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Body> login(@RequestBody @Valid Login loginDto, Errors errors) {
        System.out.println("login: " + loginDto.getEmail());
        System.out.println("login: " + loginDto.getFirebaseToken());
        return memberService.login(loginDto);
    }

    // 로그아웃
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<Body> logout(@RequestBody Logout logoutDto) {
        return memberService.logout(logoutDto);
    }

    // 자동 로그인
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "토큰 재발급(자동 로그인)")
    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@RequestBody Reissue reissueDto) {
        return memberService.reissue(reissueDto);
    }

    // 닉네임 수정
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/change_nickname")
    @Operation(summary = "닉네임 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 가지(으)로 변경되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. "),
            @ApiResponse(responseCode = "409", description = "종복된 닉네임입니다. ")
    })
    public ResponseEntity<Body> changeNickName(@RequestBody @Valid MemberRequest.NickName nickNameDto, Errors errors) {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.changeNickName(nickNameDto, errors);
    }
    // 비밀번호 변경
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/change_password")
    @Operation(summary = "비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경을 완료했습니다."),
            @ApiResponse(responseCode = "404", description = "현재 비밀번호가 일치하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "비밀번호가 일치하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> changePassword(@RequestBody @Valid MemberRequest.Password passwordDto, Errors errors){
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.changePassword(passwordDto, errors);
    }
    // 비밀번호 확인
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/check_password")
    @Operation(summary = "비밀번호 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 일치합니다."),
            @ApiResponse(responseCode = "404", description = "비밀번호가 일치하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> checkPassword(@RequestBody MemberRequest.CheckPassword password){
        return memberService.checkPassword(password);
    }
    // 회원 탈퇴
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/delete_member")
    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> deleteMember(@RequestBody MemberRequest.DeleteMember deleteMemberDto){
        return memberService.deleteMember(deleteMemberDto);
    }

    @Operation(summary = "이메일 인증")
    @PostMapping("/email-confirm")
    public ResponseEntity<Body> emailConfirm(@RequestBody @Valid MemberRequest.Email email, Errors errors) throws Exception {

        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.sendSimpleMessage(email.getEmail());
    }

    @Operation(summary = "비밀번호 찾기")
    @PostMapping("/find-password")
    public ResponseEntity<Body> findPassword(MemberRequest.IsUser isUser, Errors errors){
        return memberService.findPassword(isUser);
    }

    @Operation(summary = "닉네임 중복검사")
    @PostMapping("/check-nickname")
    public ResponseEntity<Body> checkNickName(@RequestBody @Valid MemberRequest.NickName nickName, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.checkNickName(nickName.getNickName());
    }


}
