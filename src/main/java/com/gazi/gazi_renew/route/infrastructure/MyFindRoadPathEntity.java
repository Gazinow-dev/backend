package com.gazi.gazi_renew.route.infrastructure;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gazi.gazi_renew.notification.infrastructure.NotificationEntity;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_path")
@Entity
public class  MyFindRoadPathEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int totalTime;
    private int stationTransitCount;
    private String firstStartStation;
    private String lastEndStation;

    @OneToMany(mappedBy = "myFindRoadPathEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<MyFindRoadSubPathEntity> myFindRoadPathEntity = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;
    private String name;
    private Boolean notification;

    @OneToMany(mappedBy = "myFindRoadPathEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<NotificationEntity> notificationEntities = new ArrayList<>();

    public static MyFindRoadPathEntity from(MyFindRoad myFindRoad) {
        MyFindRoadPathEntity myFindRoadPathEntity = new MyFindRoadPathEntity();
        myFindRoadPathEntity.totalTime = myFindRoad.getTotalTime();
        myFindRoadPathEntity.stationTransitCount = myFindRoad.getStationTransitCount();
        myFindRoadPathEntity.firstStartStation = myFindRoad.getFirstStartStation();
        myFindRoadPathEntity.lastEndStation = myFindRoad.getLastEndStation();
        myFindRoadPathEntity.myFindRoadPathEntity = myFindRoad.getSubPaths().stream()
                .map(MyFindRoadSubPathEntity::from)
                .collect(Collectors.toList());
        myFindRoadPathEntity.memberEntity = MemberEntity.from(myFindRoad.getMember());
        myFindRoadPathEntity.name = myFindRoad.getRoadName();
        myFindRoadPathEntity.notification = myFindRoad.getNotification();

        return myFindRoadPathEntity;
    }

    public MyFindRoad toModel() {
        return MyFindRoad.builder()
                .id(id)
                .roadName(name)
                .totalTime(totalTime)
                .stationTransitCount(stationTransitCount)
                .firstStartStation(firstStartStation)
                .lastEndStation(lastEndStation)
                .member(memberEntity.toModel())
                .subPaths(myFindRoadPathEntity.stream().map(MyFindRoadSubPathEntity::toModel)
                        .collect(Collectors.toList()))
                .notification(notification)
                .build();
    }
}