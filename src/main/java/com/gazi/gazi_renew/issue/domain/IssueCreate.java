package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class IssueCreate {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final String secretCode;
    private final String crawlingNo;
    private final IssueKeyword keyword;
    private final List<String> lines;
    private final List<Station> stations;
    private final int latestNo;
    @Builder
    public IssueCreate(Long id, String title, String content, LocalDateTime startDate, LocalDateTime expireDate, String secretCode, String crawlingNo, IssueKeyword keyword, List<String> lines, List<Station> stations, int latestNo) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.secretCode = secretCode;
        this.crawlingNo = crawlingNo;
        this.keyword = keyword;
        this.lines = lines;
        this.stations = stations;
        this.latestNo = latestNo;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Station {
        private final String line;
        private final int startStationCode;
        private final int endStationCode;
        private final IssueKeyword keyword;
        private final SubwayDirection direction;
    }
}
