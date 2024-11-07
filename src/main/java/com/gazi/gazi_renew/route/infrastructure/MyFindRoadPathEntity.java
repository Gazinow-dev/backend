package com.gazi.gazi_renew.route.infrastructure;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gazi.gazi_renew.notification.infrastructure.Notification;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_path")
@Entity
public class MyFindRoadPathEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int totalTime;
    private int stationTransitCount;
    private String firstStartStation;
    private String lastEndStation;

    @OneToMany(mappedBy = "myFindRoadPath", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<MyFindRoadSubPathEntity> subPaths = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;
    private String name;
    private Boolean notification;

    @OneToMany(mappedBy = "myFindRoadPath", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<Notification> notifications = new ArrayList<>();
}