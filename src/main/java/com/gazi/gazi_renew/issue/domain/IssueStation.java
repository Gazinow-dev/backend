package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.station.domain.Station;
import lombok.Builder;
import lombok.Getter;


@Getter
public class IssueStation {
    private final Long id;
    private final Issue issue;
    private final Station station;
    @Builder
    public IssueStation(Long id, Issue issue, Station station) {
        this.id = id;
        this.issue = issue;
        this.station = station;
    }

    public static IssueStation from(Issue issue, Station station) {

        return IssueStation.builder()
                .issue(issue)
                .station(station)
                .build();
    }
}
