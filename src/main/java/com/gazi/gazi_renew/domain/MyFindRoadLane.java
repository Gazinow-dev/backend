package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_lane")
@Entity
public class MyFindRoadLane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // 노선명
    private int stationCode; //노선코드 ex:) 2
    private String startName; //승차 정류장
    private String endName; // 하차 정류장
    @ManyToOne
    @JoinColumn(name = "my_find_road_sub_path_id", nullable = false)
    private MyFindRoadSubPath myFindRoadSubPath;
}
