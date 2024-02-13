package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_station")
@Entity
public class MyFindRoadStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int index; // 정류장 순번
    private String stationName;
//    @Column(nullable = false)
    private int stationCode;
    @ManyToOne
    @JoinColumn(name = "my_find_road_sub_path_id", nullable = false)
    private MyFindRoadSubPath myFindRoadSubPath;
}
