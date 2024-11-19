package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IssueCreate {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private String secretCode;
    private String crawlingNo;
    private IssueKeyword keyword;
    private List<String> lines;
    private List<Station> stations;
    private int latestNo;
    @Builder
    public IssueCreate(String title, String content, LocalDateTime startDate, LocalDateTime expireDate, String secretCode, String crawlingNo, IssueKeyword keyword, List<String> lines, List<Station> stations, int latestNo) {
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
    @Setter
    @NoArgsConstructor
    public static class Station {
        private String line;
        private int startStationCode;
        private int endStationCode;
        private IssueKeyword keyword;
        private SubwayDirection direction;
        @Builder
        public Station(String line, int startStationCode, int endStationCode, IssueKeyword keyword, SubwayDirection direction) {
            this.line = line;
            this.startStationCode = startStationCode;
            this.endStationCode = endStationCode;
            this.keyword = keyword;
            this.direction = direction;
        }
    }

}
