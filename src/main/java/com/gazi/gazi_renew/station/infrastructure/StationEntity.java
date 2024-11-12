package com.gazi.gazi_renew.station.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.station.domain.Station;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
//    @JsonIgnore // issues 필드를 JSON 시리얼라이제이션에서 무시
//    @ManyToMany(mappedBy = "stationEntities" ,fetch = FetchType.LAZY)
//    private List<IssueEntity> issueEntities;

    public static StationEntity from(Station station) {
        StationEntity stationEntity = new StationEntity();
        stationEntity.id = station.getId();
        stationEntity.line = station.getLine();
        stationEntity.name = station.getName();
        stationEntity.stationCode = station.getStationCode();
        stationEntity.lat = station.getLat();
        stationEntity.lng = station.getLng();
        stationEntity.issueStationCode = station.getIssueStationCode();
//        stationEntity.issueEntities = station.getIssueList().stream()
//                .map(IssueEntity::from).collect(Collectors.toList());
        return stationEntity;
    }
    public Station toModel() {
//        List<Issue> issueList = issueEntities.stream().map(IssueEntity::toModel)
//                .collect(Collectors.toList());
        return Station.builder()
                .id(id)
                .line(line)
                .name(name)
                .stationCode(stationCode)
                .lat(lat)
                .lng(lng)
//                .issueList(issueList)
                .build();
    }
}
