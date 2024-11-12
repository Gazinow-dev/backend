package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @OneToMany(mappedBy = "myFindRoadSubPathEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MyFindRoadLaneEntity> lanes = new ArrayList<>();
    @OneToMany(mappedBy = "myFindRoadSubPathEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
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
                .id(id)
                .trafficType(trafficType)
                .distance(distance)
                .sectionTime(sectionTime)
                .stationCount(stationCount)
                .door(door)
                .way(way)
                .lanes(lanes.stream().map(MyFindRoadLaneEntity::toModel).collect(Collectors.toList()))
                .stations(stations.stream().map(MyFindRoadStationEntity::toModel).collect(Collectors.toList()))
                .build();
    }

}
