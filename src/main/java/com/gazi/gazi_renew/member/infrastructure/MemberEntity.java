package com.gazi.gazi_renew.member.infrastructure;

import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.issue.infrastructure.LikeEntity;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.member.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class MemberEntity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column
    private OAuthProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    // 푸시 알림 관련 필드 추가
    private Boolean pushNotificationEnabled; // 푸시 알림 받기 여부

    private Boolean mySavedRouteNotificationEnabled; // 내가 저장한 경로 알림 여부

    private String firebaseToken;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시
    public static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.getId();
        memberEntity.email = member.getEmail();
        memberEntity.password = member.getPassword();
        memberEntity.nickName = member.getNickName();
        memberEntity.provider = member.getProvider();
        memberEntity.role = member.getRole();
        memberEntity.pushNotificationEnabled = member.getPushNotificationEnabled();
        memberEntity.mySavedRouteNotificationEnabled = member.getMySavedRouteNotificationEnabled();
        memberEntity.firebaseToken = member.getFirebaseToken();
        memberEntity.createdAt = member.getCreatedAt();
        return memberEntity;
    }

    public Member toModel() {
        return Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickName(nickName)
                .provider(provider)
                .role(role)
                .pushNotificationEnabled(pushNotificationEnabled)
                .mySavedRouteNotificationEnabled(mySavedRouteNotificationEnabled)
                .firebaseToken(firebaseToken)
                .createdAt(createdAt)
                .build();
    }

}
