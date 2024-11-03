package com.gazi.gazi_renew.station.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "station",  indexes = {
        @Index(name = "line", columnList = "line"),
        @Index(name = "name", columnList = "name")
})
@Entity
public class StationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String line;
    private String name;
    @Column(nullable = false)
    private int stationCode;
    private double lat;
    private double lng;
    private Integer issueStationCode;
    @JsonIgnore // issues 필드를 JSON 시리얼라이제이션에서 무시
    @ManyToMany(mappedBy = "stations" ,fetch = FetchType.LAZY)
    private List<IssueEntity> issueEntities;


}
