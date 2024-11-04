package com.gazi.gazi_renew.member.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.member.controller.response.MemberResponse;
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


@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController extends BaseController {
    private final MemberService memberService;
    private final Response response

    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("signup")
    public ResponseEntity<Body> signup(@RequestBody @Valid MemberCreate memberCreate, Errors errors) {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        Member member = memberService.signUp(memberCreate, errors);
        // 로그인 까지 진행
        memberService.login(memberCreate.toMemberLogin());
        return response.success(MemberResponse.from(member), "회원가입이 완료되었습니다", HttpStatus.OK);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Body> login(@RequestBody @Valid MemberLogin memberLogin, Errors errors) {
        return memberService.login(memberLogin);
    }

    // 로그아웃
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<Body> logout(@RequestBody MemberLogout memberLogout) {
        return memberService.logout(memberLogout);
    }

    // 자동 로그인
    @Operation(summary = "토큰 재발급(자동 로그인)")
    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@RequestBody MemberReissue memberReissue) {
        return memberService.reissue(memberReissue);
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
    public ResponseEntity<Body> changeNickName(@RequestBody @Valid MemberNicknameValidation memberNicknameValidation, Errors errors) {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.changeNickName(memberNicknameValidation, errors);
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
            return memberService.validateHandling(errors);
        }
        return memberService.changePassword(memberChangePassword, errors);
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
        return memberService.checkPassword(memberCheckPassword);
    }
    // 회원 탈퇴
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/delete_member")
    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> deleteMember(@RequestBody Member.DeleteMember deleteMemberDto){
        return memberService.deleteMember(deleteMemberDto);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/fcm-token")
    @Operation(summary = "FireBase 토큰 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "FireBase 토큰 저장 완료."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> saveFcmToken(@RequestBody MemberFcmToken memberFcmToken) {
        return memberService.saveFcmToken(memberFcmToken);
    }

    @Operation(summary = "이메일 인증")
    @PostMapping("/email-confirm")
    public ResponseEntity<Body> emailConfirm(@RequestBody @Valid MemberEmailValidation memberEmailValidation, Errors errors) throws Exception {

        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.sendSimpleMessage(memberEmailValidation.getEmail());
    }

    @Operation(summary = "비밀번호 찾기")
    @PostMapping("/find-password")
    public ResponseEntity<Body> findPassword(IsMember isMember, Errors errors){
        return memberService.findPassword(isMember);
    }

    @Operation(summary = "닉네임 중복검사")
    @PostMapping("/check-nickname")
    public ResponseEntity<Body> checkNickName(@RequestBody @Valid MemberNicknameValidation memberNicknameValidation, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.checkNickName(memberNicknameValidation.getNickname());
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "푸시 알림 on/off 설정")
    @PostMapping("/notifications/push")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "푸시 알림 수신 설정이 저장되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> updatePushNotificationStatus(@RequestBody @Valid MemberAlertAgree memberAlertAgree, Errors errors) {
        return memberService.updatePushNotificationStatus(memberAlertAgree);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "푸시 알림 on/off 설정 조회")
    @GetMapping("/notifications/push/status")
    public ResponseEntity<Body> getPushNotificationStatus(@RequestParam String email) {
        return memberService.getPushNotificationStatus(email);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "내가 저장한 경로 알림 on/off 설정")
    @PostMapping("/notifications/my-saved-route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 저장한 경로 알림 수신 설정이 저장되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> updateMySavedRouteNotificationStatus(@RequestBody @Valid MemberAlertAgree memberAlertAgree, Errors errors) {
        return memberService.updateMySavedRouteNotificationStatus(memberAlertAgree);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "내가 저장한 경로 알림 on/off 설정 조회")
    @GetMapping("/notifications/my-saved-route/status")
    public ResponseEntity<Body> getMySavedRouteNotificationStatus(@RequestParam String email) {
        return memberService.getMySavedRouteNotificationStatus(email);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "경로 상세 설정 알림 on/off 설정")
    @PostMapping("/notifications/route-detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경로 상세 설정 알림 수신 설정이 저장되었습니다."),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다. ")
    })
    public ResponseEntity<Body> updateRouteDetailNotificationStatus(@RequestBody @Valid MemberAlertAgree memberAlertAgree, Errors errors) {
        return memberService.updateRouteDetailNotificationStatus(memberAlertAgree);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "경로 상세 설정 알림 on/off 설정 조회")
    @GetMapping("/notifications/route-detail/status")
    public ResponseEntity<Body> getRouteDetailNotificationStatus(@RequestParam String email) {
        return memberService.getRouteDetailNotificationStatus(email);
    }

}
