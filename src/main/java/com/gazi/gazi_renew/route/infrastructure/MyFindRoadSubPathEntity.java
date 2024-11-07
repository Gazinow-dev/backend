package com.gazi.gazi_renew.route.infrastructure;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
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
}
