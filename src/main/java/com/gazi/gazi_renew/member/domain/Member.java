package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.domain.dto.MemberCheckPassword;
import com.gazi.gazi_renew.member.domain.dto.MemberCreate;
import com.gazi.gazi_renew.member.domain.dto.MemberLogin;
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
    private final List<RecentSearch> recentSearchList;
    @Builder
    public Member(Long id, String email, String password, String nickName, OAuthProvider provider, Role role, Boolean pushNotificationEnabled, Boolean mySavedRouteNotificationEnabled, Boolean routeDetailNotificationEnabled, String firebaseToken, LocalDateTime createdAt, List<RecentSearch> recentSearchList) {
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
        this.recentSearchList = recentSearchList;
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
    public boolean isMatchesPassword(PasswordEncoder passwordEncoder, MemberCheckPassword checkPassword, Member member) {
        return passwordEncoder.matches(checkPassword.getCheckPassword(), member.getPassword());
    }
}
