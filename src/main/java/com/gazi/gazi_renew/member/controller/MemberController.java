package com.gazi.gazi_renew.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.aspect.TrackEvent;
import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.member.controller.response.MemberAlertAgreeResponse;
import com.gazi.gazi_renew.member.controller.response.MemberNicknameResponse;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.common.controller.response.Response.Body;
import com.gazi.gazi_renew.member.controller.port.MemberService;
import com.gazi.gazi_renew.member.domain.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController extends BaseController {
    private final MemberService memberService;
    private final Response response;

    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "422", description = "금칙어가 포함되어 사용할 수 없습니다.")
    })
    @PostMapping("signup")
    public ResponseEntity<Body> signup(@RequestBody @Valid MemberCreate memberCreate, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
        }
        Member member = memberService.signUp(memberCreate, errors);
        // 로그인 까지 진행
        ResponseToken responseToken = memberService.login(memberCreate.toMemberLogin());
        return response.success(responseToken, "회원가입이 완료되었습니다", HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Body> login(@RequestBody @Valid MemberLogin memberLogin, Errors errors) {
        ResponseToken responseToken = memberService.login(memberLogin);
        return response.success(responseToken, "로그인에 성공했습니다.", HttpStatus.OK);
    }

    // 로그아웃
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<Body> logout(@RequestBody MemberLogout memberLogout) {
        memberService.logout(memberLogout);
        return response.success("로그아웃 되었습니다.");
    }

    // 자동 로그인
    @Operation(summary = "토큰 재발급(자동 로그인)")
    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@RequestBody MemberReissue memberReissue) {
        ResponseToken responseToken = memberService.reissue(memberReissue);
        return response.success(responseToken, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    // 닉네임 수정
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/change_nickname")
    @Operation(summary = "닉네임 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 가지(으)로 변경되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. "),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임입니다. "),
            @ApiResponse(responseCode = "422", description = "금칙어가 포함되어 사용할 수 없습니다.")
    })
    public ResponseEntity<Body> changeNickName(@RequestBody @Valid MemberNicknameValidation memberNicknameValidation, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
        }
        Member member = memberService.changeNickName(memberNicknameValidation, errors);
        return response.success(MemberNicknameResponse.from(member), "닉네임 변경 완료",HttpStatus.OK);
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
    public ResponseEntity<Body> changePassword(@RequestBody @Valid MemberChangePassword memberChangePassword, Errors errors){
        if (errors.hasErrors()) {
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
        }
        memberService.changePassword(memberChangePassword, errors);
        return response.success("비밀번호 변경을 완료했습니다.");
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
    public ResponseEntity<Body> checkPassword(@RequestBody MemberCheckPassword memberCheckPassword){
        boolean state = memberService.checkPassword(memberCheckPassword);
        if (state) {
            return response.success("비밀번호가 일치합니다.");
        } else {
            return response.fail("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    // 회원 탈퇴
    @SecurityRequirement(name = "Bearer Authentication")
    @TrackEvent("DELETE_MEMBER")
    @DeleteMapping("/delete_member")
    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> deleteMember(@RequestBody MemberDelete memberDelete){
        memberService.deleteMember(memberDelete);
        return response.success("회원 탈퇴 완료.");
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/fcm-token")
    @Operation(summary = "FireBase 토큰 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "FireBase 토큰 저장 완료."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> saveFcmToken(@RequestBody MemberFcmToken memberFcmToken) {
        memberService.saveFcmToken(memberFcmToken);
        return response.success("FireBase 토큰 저장 완료.");
    }

    @Operation(summary = "이메일 인증")
    @PostMapping("/email-confirm")
    public ResponseEntity<Body> emailConfirm(@RequestBody @Valid MemberEmailValidation memberEmailValidation, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
        }
        String keyValue = memberService.sendSimpleMessage(memberEmailValidation.getEmail());
        return response.success(keyValue,"인증번호를 발송하였습니다.", HttpStatus.OK);

    }

    @Operation(summary = "비밀번호 찾기")
    @PostMapping("/find-password")
    public ResponseEntity<Body> findPassword(IsMember isMember, Errors errors){
        String password = memberService.findPassword(isMember);
        return response.success("임시비밀번호 발급: " +password);
    }

    @Operation(summary = "닉네임 중복검사")
    @PostMapping("/check-nickname")
    public ResponseEntity<Body> checkNickName(@RequestBody @Valid MemberNicknameValidation memberNicknameValidation, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
        }
        boolean checked = memberService.checkNickName(memberNicknameValidation.getNickname());
        if (checked) {
            return response.success(memberNicknameValidation.getNickname(),"사용가능한 닉네임입니다.", HttpStatus.OK);
        }
        else{
            return response.fail("중복된 닉네임입니다.", HttpStatus.CONFLICT);
        }
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "푸시 알림 on/off 설정")
    @PostMapping("/notifications/push")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "푸시 알림 수신 설정이 저장되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> updatePushNotificationStatus(@RequestBody @Valid MemberAlertAgree memberAlertAgree, Errors errors) throws JsonProcessingException {
        memberService.updatePushNotificationStatus(memberAlertAgree);
        return response.success("푸시 알림 수신 설정이 저장되었습니다.");
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "푸시 알림 on/off 설정 조회")
    @GetMapping("/notifications/push/status")
    public ResponseEntity<Body> getPushNotificationStatus(@RequestParam String email) {
        Member member = memberService.getPushNotificationStatus(email);
        return response.success(MemberAlertAgreeResponse.pushAlertAgreeFrom(member), "", HttpStatus.OK);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "내가 저장한 경로 알림 on/off 설정")
    @PostMapping("/notifications/my-saved-route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 저장한 경로 알림 수신 설정이 저장되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> updateMySavedRouteNotificationStatus(@RequestBody @Valid MemberAlertAgree memberAlertAgree, Errors errors) throws JsonProcessingException {
        memberService.updateMySavedRouteNotificationStatus(memberAlertAgree);
        return response.success("내가 저장한 경로 알림 수신 설정이 저장되었습니다.");
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "내가 저장한 경로 알림 on/off 설정 조회")
    @GetMapping("/notifications/my-saved-route/status")
    public ResponseEntity<Body> getMySavedRouteNotificationStatus(@RequestParam String email) {
        Member member = memberService.getMySavedRouteNotificationStatus(email);
        return response.success(MemberAlertAgreeResponse.mySavedRouteAlertAgreeFrom(member), "", HttpStatus.OK);
    }

}
