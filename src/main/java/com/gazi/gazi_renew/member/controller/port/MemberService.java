package com.gazi.gazi_renew.member.controller.port;


import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.common.controller.response.Response.Body;
import com.gazi.gazi_renew.member.domain.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;


public interface MemberService {
    // 회원가입
    Member signUp(@Valid MemberCreate memberCreate, Errors errors);

    void validateEmail(String email);
    void validateNickName(String nickName);

        // 로그인
    ResponseToken login(MemberLogin loginDto);

    // 로그아웃
    Member logout(MemberLogout memberLogout);

    // 자동 로그인
    ResponseToken reissue(MemberReissue memberReissue);

    // 닉네임 수정
    Member changeNickName(@Valid MemberNicknameValidation memberNicknameValidation, Errors errors);

    // 비밀번호 확인
    boolean checkPassword(MemberCheckPassword checkPassword);

    ResponseEntity<Body> findPassword(Member.IsUser isUserRequest);

    // 비밀번호 변경
    ResponseEntity<Body> changePassword(@Valid Member.Password passwordDto, Errors errors);
    // 회원 탈퇴
    ResponseEntity<Body> deleteMember(Member.DeleteMember deleteMemberDto);
    /* 회원가입 시, 유효성 체크 */
    ResponseEntity<Body> validateHandling(Errors errors);

    ResponseEntity<Body> sendSimpleMessage(String email) throws Exception;

    ResponseEntity<Body> checkEmail(String email);

    ResponseEntity<Body> checkNickName(String nickName);

    ResponseEntity<Body> updatePushNotificationStatus(Member.AlertAgree alertAgreeRequest);

    ResponseEntity<Body> updateMySavedRouteNotificationStatus(Member.AlertAgree alertAgreeRequest);

    ResponseEntity<Body> updateRouteDetailNotificationStatus(Member.AlertAgree alertAgreeRequest);

    ResponseEntity<Body> getPushNotificationStatus(String email);

    ResponseEntity<Body> getMySavedRouteNotificationStatus(String email);

    ResponseEntity<Body> getRouteDetailNotificationStatus(String email);

    ResponseEntity<Body> saveFcmToken(Member.FcmTokenRequest fcmTokenRequest);
}
