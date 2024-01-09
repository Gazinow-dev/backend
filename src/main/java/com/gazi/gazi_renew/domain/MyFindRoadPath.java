package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "myFindRoadPath", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MyFindRoadSubPath> subPaths = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private String name;
}
