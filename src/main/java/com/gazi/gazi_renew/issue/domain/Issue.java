package com.gazi.gazi_renew.issue.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.Station;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Issue {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final String secretCode;
    private final String crawlingNo;
    private final IssueKeyword keyword;
    private final List<String> lines;
    private final List<IssueStation> issueStations;
    private final int latestNo;
    private final int likeCount;
    @Builder
    public Issue(Long id, String title, String content, LocalDateTime startDate, LocalDateTime expireDate, String secretCode, String crawlingNo, IssueKeyword keyword, List<String> lines, List<IssueStation> issueStations, int latestNo, int likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.secretCode = secretCode;
        this.crawlingNo = crawlingNo;
        this.keyword = keyword;
        this.lines = lines;
        this.issueStations = issueStations;
        this.latestNo = latestNo;
        this.likeCount = likeCount;
    }
    public Issue update(IssueUpdate issueUpdate) {
        return Issue.builder()
                .id(this.id)
                .title(this.title)
                .content(issueUpdate.getContent()) // 변경된 content 반영
                .startDate(this.startDate)
                .expireDate(this.expireDate)
                .secretCode(this.secretCode)
                .crawlingNo(this.crawlingNo)
                .keyword(this.keyword)
                .lines(this.lines)
                .issueStations(this.issueStations)
                .latestNo(this.latestNo)
                .likeCount(this.likeCount)
                .build();
    }

    public static Issue from(IssueCreate issueCreate, List<Station> stationList) {
        List<IssueStation> issueStationList = stationList.stream()
                .map(station -> new IssueStation(
                        station.getLine(),
                        station.getName()))
                .collect(Collectors.toList());

        return Issue.builder()
                .title(issueCreate.getTitle())
                .content(issueCreate.getContent())
                .startDate(issueCreate.getStartDate())
                .expireDate(issueCreate.getExpireDate())
                .secretCode(issueCreate.getSecretCode())
                .crawlingNo(issueCreate.getCrawlingNo())
                .keyword(issueCreate.getKeyword())
                .lines(issueCreate.getLines())
                .issueStations(issueStationList)
                .latestNo(issueCreate.getLatestNo())
                .build();
    }
    @Getter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class IssueStation {
        private final String line;
        private final String stationName;
        @Builder
        public IssueStation(String line, String stationName) {
            this.line = line;
            this.stationName = stationName;
        }
    }
}
