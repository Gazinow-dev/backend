package com.gazi.gazi_renew.member.infrastructure;

import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.issue.infrastructure.LikeEntity;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import com.gazi.gazi_renew.member.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MEMBER")
@Entity
public class MemberEntity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
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

    private Boolean routeDetailNotificationEnabled; // 경로 상세 설정 알림 여부

    private String firebaseToken;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RecentSearchEntity> recentSearchEntities = new LinkedList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<MyFindRoadPathEntity> myFindRoadPathEntities = new LinkedList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikeEntity> likeEntities = new HashSet<>();

    public MemberEntity saveFcmToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
        return this;
    }

    @Builder
    public MemberEntity(String email, String nickname, OAuthProvider provider) {
        this.email = email;
        this.nickName = nickname;
        this.provider = provider;
    }

}
