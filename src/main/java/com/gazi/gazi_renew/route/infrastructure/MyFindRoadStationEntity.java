package com.gazi.gazi_renew.route.infrastructure;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_station")
@Entity
public class MyFindRoadStationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int index; // 정류장 순번
    private String stationName;
    @ManyToOne
    @JoinColumn(name = "my_find_road_sub_path_id", nullable = false)
    private MyFindRoadSubPathEntity myFindRoadSubPathEntity;
}
