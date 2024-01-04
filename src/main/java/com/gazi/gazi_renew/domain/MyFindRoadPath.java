package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_load_path")
@Entity
public class MyFindRoadPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int totalTime; // 총소요시간
    private int subwayTransitCount;
    private String firstStartStation;
    private String lastEndStation;
    @ManyToOne
    @JoinColumn(name = "my_find_road_id", nullable = false)
    private MyFindRoad myFindRoad;
}
