package com.gazi.gazi_renew.member.controller.port;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.common.controller.response.Response.Body;
import com.gazi.gazi_renew.member.domain.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.util.Map;


public interface MemberService {
    // 회원가입
    Member signUp(@Valid MemberCreate memberCreate, Errors errors) throws Exception;

    void validateEmail(String email);
    void validateNickName(String nickName) throws Exception;

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

    String findPassword(IsMember isMember);

    // 비밀번호 변경
    Member changePassword(@Valid MemberChangePassword memberChangePassword, Errors errors);
    // 회원 탈퇴
    Member deleteMember(MemberDelete memberDelete);
    /* 회원가입 시, 유효성 체크 */
    Map<String, String> validateHandling(Errors errors);

    String sendSimpleMessage(String email) throws Exception;

    boolean checkEmail(String email);

    boolean checkNickName(String nickName);

    Member updatePushNotificationStatus(MemberAlertAgree memberAlertAgreee) throws JsonProcessingException;

    Member updateMySavedRouteNotificationStatus(MemberAlertAgree memberAlertAgree) throws JsonProcessingException;

    Member getPushNotificationStatus(String email);

    Member getMySavedRouteNotificationStatus(String email);

    Member saveFcmToken(MemberFcmToken memberFcmToken);
}
