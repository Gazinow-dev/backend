package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import jakarta.persistence.*;
import lombok.*;


@Getter
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
    @Column(name = "member_id", nullable = true)
    private Long memberId;
    private String name;
    private Boolean notification;

    public static MyFindRoadPathEntity from(MyFindRoad myFindRoad) {
        MyFindRoadPathEntity myFindRoadPathEntity = new MyFindRoadPathEntity();
        myFindRoadPathEntity.id = myFindRoad.getId();
        myFindRoadPathEntity.totalTime = myFindRoad.getTotalTime();
        myFindRoadPathEntity.stationTransitCount = myFindRoad.getStationTransitCount();
        myFindRoadPathEntity.firstStartStation = myFindRoad.getFirstStartStation();
        myFindRoadPathEntity.lastEndStation = myFindRoad.getLastEndStation();
        myFindRoadPathEntity.memberId = myFindRoad.getMemberId();
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
                .memberId(memberId)
                .notification(notification)
                .build();
    }
}