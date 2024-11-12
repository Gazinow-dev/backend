package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.domain.dto.MemberCheckPassword;
import com.gazi.gazi_renew.member.domain.dto.MemberCreate;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.oauth.controller.response.OAuthInfoResponse;
import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.member.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Member {
    private final Long id;
    private final String email;
    private final String password;
    private final String nickName;
    private final OAuthProvider provider;
    private final Role role;
    private final Boolean pushNotificationEnabled;
    private final Boolean mySavedRouteNotificationEnabled;
    private final Boolean routeDetailNotificationEnabled;
    private final String firebaseToken;
    private final LocalDateTime createdAt;
    @Builder
    public Member(Long id, String email, String password, String nickName, OAuthProvider provider, Role role, Boolean pushNotificationEnabled, Boolean mySavedRouteNotificationEnabled, Boolean routeDetailNotificationEnabled, String firebaseToken, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.provider = provider;
        this.role = role;
        this.pushNotificationEnabled = pushNotificationEnabled;
        this.mySavedRouteNotificationEnabled = mySavedRouteNotificationEnabled;
        this.routeDetailNotificationEnabled = routeDetailNotificationEnabled;
        this.firebaseToken = firebaseToken;
        this.createdAt = createdAt;
    }

    public static Member saveSocialLoginMember(OAuthInfoResponse oAuthInfoResponse, PasswordEncoder passwordEncoder) {
        String email = oAuthInfoResponse.getEmail();
        String nickname = oAuthInfoResponse.getNickname();
        // 소셜로그인으로 회원 가입 시 nickname이 null일 경우 임의로 메일의 id로 대체
        if (nickname == null || nickname.isEmpty()) {
            nickname = email.substring(0, email.indexOf("@"));
        }

        return Member.builder()
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .email(email)
                .password(passwordEncoder.encode("dummy"))
                .role(Role.valueOf("ROLE_USER"))
                .nickName(nickname)
                .provider(oAuthInfoResponse.getOAuthProvider())
                .build();
    }
    public static Member from(MemberCreate memberCreate, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(memberCreate.getEmail())
                .password(passwordEncoder.encode(memberCreate.getPassword()))
                .nickName(memberCreate.getNickName())
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken(memberCreate.getFirebasetoken())
                .build();
    }
    public Member saveFireBaseToken(String firebaseToken) {
        return Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickName(nickName)
                .role(role)
                .pushNotificationEnabled(pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(mySavedRouteNotificationEnabled)
                .routeDetailNotificationEnabled(routeDetailNotificationEnabled)
                .firebaseToken(firebaseToken)
                .build();
    }
    // 닉네임 변경
    public Member changeNickname(String nickname) {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickName(nickname)
                .role(this.role)
                .pushNotificationEnabled(this.pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(this.mySavedRouteNotificationEnabled)
                .routeDetailNotificationEnabled(this.routeDetailNotificationEnabled)
                .firebaseToken(this.firebaseToken)
                .build();
    }
    public Member changePassword(PasswordEncoder passwordEncoder, String tempPassword) {
        return Member.builder()
                .email(this.email)
                .password(passwordEncoder.encode(tempPassword))
                .nickName(this.nickName)
                .role(this.role)
                .pushNotificationEnabled(this.pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(this.mySavedRouteNotificationEnabled)
                .routeDetailNotificationEnabled(this.routeDetailNotificationEnabled)
                .firebaseToken(this.firebaseToken)
                .build();
    }
    public Member updatePushNotificationEnabled(boolean alertAgree) {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickName(this.nickName)
                .role(this.role)
                .pushNotificationEnabled(alertAgree)
                .mySavedRouteNotificationEnabled(this.mySavedRouteNotificationEnabled)
                .routeDetailNotificationEnabled(this.routeDetailNotificationEnabled)
                .firebaseToken(this.firebaseToken)
                .build();
    }
    public Member updateMySavedRouteNotificationEnabled(boolean alertAgree) {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickName(this.nickName)
                .role(this.role)
                .pushNotificationEnabled(this.pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(alertAgree)
                .routeDetailNotificationEnabled(this.routeDetailNotificationEnabled)
                .firebaseToken(this.firebaseToken)
                .build();
    }
    public Member updateRouteDetailNotificationEnabled(boolean alertAgree) {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickName(this.nickName)
                .role(this.role)
                .pushNotificationEnabled(this.pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(this.mySavedRouteNotificationEnabled)
                .routeDetailNotificationEnabled(alertAgree)
                .firebaseToken(this.firebaseToken)
                .build();
    }

    public Member saveFcmToken(String firebaseToken) {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickName(this.nickName)
                .role(this.role)
                .pushNotificationEnabled(this.pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(this.mySavedRouteNotificationEnabled)
                .routeDetailNotificationEnabled(this.routeDetailNotificationEnabled)
                .firebaseToken(firebaseToken)
                .build();
    }
    //비밀번호 일치 여부
    public boolean isMatchesPassword(PasswordEncoder passwordEncoder, MemberCheckPassword checkPassword, Member member) {
        return passwordEncoder.matches(checkPassword.getCheckPassword(), member.getPassword());
    }

    //랜덤함수로 임시비밀번호 구문 만들기
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        char[] specialSet = new char[] {'!', '#', '$', '%', '&','~'};


        String newPassword = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            idx = (int) (charSet.length * Math.random());
            newPassword += charSet[idx];
        }

        for (int i = 0; i < 2 ; i++) {
            idx = (int) (specialSet.length * Math.random());
            newPassword += specialSet[idx];
        }
        return newPassword;
    }
}
