package com.gazi.gazi_renew.domain;

import com.gazi.gazi_renew.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MEMBER")
@Entity
public class Member extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String nickName;
    @Column
    private String provider;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false)
    private Boolean isAgree;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecentSearch> recentSearches = new LinkedList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MyFindRoadPath> myFindRoadPaths = new LinkedList<>();

    public void addRecentSearch(RecentSearch recentSearch) {
        recentSearch.setMember(this);
        recentSearches.add(recentSearch);
    }

    public Member update(String nickName, String email, String provider) {
        this.nickName = nickName;
        this.email = email;
        this.provider = provider;
        return this;
    }

}
