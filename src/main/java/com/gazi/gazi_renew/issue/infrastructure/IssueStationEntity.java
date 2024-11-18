package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue_station")
@Entity
public class IssueStationEntity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private IssueEntity issueEntity;
    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity stationEntity;

    public static IssueStationEntity from(IssueStation issueStation) {
        IssueStationEntity issueStationEntity = new IssueStationEntity();
        issueStationEntity.id = issueStation.getId();
        issueStationEntity.issueEntity = IssueEntity.from(issueStation.getIssue());
        issueStationEntity.stationEntity = StationEntity.from(issueStation.getStation())
        ;
        return issueStationEntity;
    }
    public IssueStation toModel() {
        return IssueStation.builder()
                .id(id)
                .issue(issueEntity.toModel())
                .station(stationEntity.toModel())
                .build();
    }
}
