package com.gazi.gazi_renew.member.domain;

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
                .password(passwordEncoder.encode(password))
                .nickName(memberCreate.getNickName())
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken(memberCreate.getFirebasetoken())
                .build();
    }
}
