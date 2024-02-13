package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "station",  indexes = {
        @Index(name = "line", columnList = "line"),
        @Index(name = "name", columnList = "name")
})
@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String line;
    private String name;
    @Column(nullable = false)
    private int stationCode;
    private double lat;
    private double lng;
    @ManyToMany(mappedBy = "stations" ,fetch = FetchType.LAZY)
    private List<Issue> issues;
}
