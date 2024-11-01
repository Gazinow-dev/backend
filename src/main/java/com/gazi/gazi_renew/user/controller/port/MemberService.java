package com.gazi.gazi_renew.user.controller.port;


import com.gazi.gazi_renew.user.domain.MemberRequest;
import com.gazi.gazi_renew.common.controller.response.Response.Body;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;


public interface MemberService {
    // 회원가입
    ResponseEntity<Body> signUp(@Valid MemberRequest.SignUp signUpDto, Errors errors);

    void validateEmail(String email);
    void validateNickName(String nickName);

        // 로그인
    ResponseEntity<Body> login(MemberRequest.Login loginDto);

    // 로그아웃
    ResponseEntity<Body> logout(MemberRequest.Logout logoutDto);

    // 자동 로그인
    ResponseEntity<Body> reissue(MemberRequest.Reissue reissueDto);

    // 닉네임 수정
    ResponseEntity<Body> changeNickName(@Valid MemberRequest.NickName nickNameDto, Errors errors);

    // 비밀번호 확인
    ResponseEntity<Body> checkPassword(MemberRequest.CheckPassword checkPassword);

    ResponseEntity<Body> findPassword(MemberRequest.IsUser isUserRequest);

    // 비밀번호 변경
    ResponseEntity<Body> changePassword(@Valid MemberRequest.Password passwordDto, Errors errors);
    // 회원 탈퇴
    ResponseEntity<Body> deleteMember(MemberRequest.DeleteMember deleteMemberDto);
    /* 회원가입 시, 유효성 체크 */
    @Transactional(readOnly = true)
    ResponseEntity<Body> validateHandling(Errors errors);

    ResponseEntity<Body> sendSimpleMessage(String email) throws Exception;

    ResponseEntity<Body> checkEmail(String email);

    ResponseEntity<Body> checkNickName(String nickName);

    ResponseEntity<Body> updatePushNotificationStatus(MemberRequest.AlertAgree alertAgreeRequest);

    ResponseEntity<Body> updateMySavedRouteNotificationStatus(MemberRequest.AlertAgree alertAgreeRequest);

    ResponseEntity<Body> updateRouteDetailNotificationStatus(MemberRequest.AlertAgree alertAgreeRequest);

    ResponseEntity<Body> getPushNotificationStatus(String email);

    ResponseEntity<Body> getMySavedRouteNotificationStatus(String email);

    ResponseEntity<Body> getRouteDetailNotificationStatus(String email);

    ResponseEntity<Body> saveFcmToken(MemberRequest.FcmTokenRequest fcmTokenRequest);
}
