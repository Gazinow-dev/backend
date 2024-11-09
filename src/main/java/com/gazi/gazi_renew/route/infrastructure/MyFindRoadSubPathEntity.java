package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_sub_path")
@Entity
public class MyFindRoadSubPathEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int trafficType; //이동수단 종류
    private double distance; //이동거리
    private int sectionTime; //이동 소요 시간
    private int stationCount; // 정차하는 역 개수
    private String door;
    private String way;
    @ManyToOne
    @JoinColumn(name = "my_find_road_path_id", nullable = false)
    private MyFindRoadPathEntity myFindRoadPathEntity;
    @OneToMany(mappedBy = "myFindRoadSubPath", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MyFindRoadLaneEntity> lanes = new ArrayList<>();
    @OneToMany(mappedBy = "myFindRoadSubPath", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MyFindRoadStationEntity> stations = new ArrayList<>();

    public static MyFindRoadSubPathEntity from(MyFindRoadSubPath myFindRoadSubPath) {
        MyFindRoadSubPathEntity myFindRoadSubPathEntity = new MyFindRoadSubPathEntity();
        myFindRoadSubPathEntity.trafficType = myFindRoadSubPath.getTrafficType();
        myFindRoadSubPathEntity.distance = myFindRoadSubPath.getDistance();
        myFindRoadSubPathEntity.sectionTime = myFindRoadSubPath.getSectionTime();
        myFindRoadSubPathEntity.stationCount = myFindRoadSubPath.getStationCount();
        myFindRoadSubPathEntity.door = myFindRoadSubPath.getDoor();
        myFindRoadSubPathEntity.way = myFindRoadSubPath.getWay();

        return myFindRoadSubPathEntity;
    }
    public MyFindRoadSubPath toModel() {
        return MyFindRoadSubPath.builder()
                .trafficType(trafficType)
                .distance(distance)
                .sectionTime(sectionTime)
                .stationCount(stationCount)
                .door(door)
                .way(way)
                .build();
    }
}
