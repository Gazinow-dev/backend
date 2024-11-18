package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import jakarta.persistence.*;
import lombok.*;


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
    private String name; // 노선명
    private int stationCode; //노선코드 ex:) 2
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_find_road_path_id")
    private MyFindRoadPathEntity myFindRoadPathEntity;

    public static MyFindRoadSubPathEntity from(MyFindRoadSubPath myFindRoadSubPath) {
        MyFindRoadSubPathEntity myFindRoadSubPathEntity = new MyFindRoadSubPathEntity();
        myFindRoadSubPathEntity.id = myFindRoadSubPath.getId();
        myFindRoadSubPathEntity.trafficType = myFindRoadSubPath.getTrafficType();
        myFindRoadSubPathEntity.distance = myFindRoadSubPath.getDistance();
        myFindRoadSubPathEntity.sectionTime = myFindRoadSubPath.getSectionTime();
        myFindRoadSubPathEntity.stationCount = myFindRoadSubPath.getStationCount();
        myFindRoadSubPathEntity.door = myFindRoadSubPath.getDoor();
        myFindRoadSubPathEntity.way = myFindRoadSubPath.getWay();
        myFindRoadSubPathEntity.name = myFindRoadSubPath.getName();
        myFindRoadSubPathEntity.stationCode = myFindRoadSubPath.getStationCode();
        myFindRoadSubPathEntity.myFindRoadPathEntity = MyFindRoadPathEntity.from(myFindRoadSubPath.getMyFindRoad());
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
                .name(name)
                .stationCode(stationCode)
                .myFindRoad(myFindRoadPathEntity.toModel())
                .build();
    }

}
