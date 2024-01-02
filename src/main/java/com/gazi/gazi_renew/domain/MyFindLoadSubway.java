package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_load_subway")
@Entity
public class MyFindLoadSubway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int index; // 정류장 순번
    private String stationName;
    @ManyToOne
    @JoinColumn(name = "my_find_load_sub_path_id", nullable = false)
    private MyFindLoadSubPath myFindLoadSubPath;
}
