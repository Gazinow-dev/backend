package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_load_station")
@Entity
public class MyFindLoadStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stationName;
    private String stationLine;
    private String station_status; // 출발역,환승역,도착역
    @ManyToOne
    @JoinColumn(name = "my_find_load_id", nullable = false)
    private MyFindLoad myFindLoad;
}
