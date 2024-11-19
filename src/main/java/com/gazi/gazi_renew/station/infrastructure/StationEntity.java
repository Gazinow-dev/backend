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

    public static StationEntity from(Station station) {
        StationEntity stationEntity = new StationEntity();
        stationEntity.id = station.getId();
        stationEntity.line = station.getLine();
        stationEntity.name = station.getName();
        stationEntity.stationCode = station.getStationCode();
        stationEntity.lat = station.getLat();
        stationEntity.lng = station.getLng();
        stationEntity.issueStationCode = station.getIssueStationCode();
        return stationEntity;
    }
    public Station toModel() {
        return Station.builder()
                .id(id)
                .line(line)
                .name(name)
                .stationCode(stationCode)
                .lat(lat)
                .lng(lng)
                .build();
    }
}
