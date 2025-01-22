package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
public class ExternalIssueCreate {
    private final String title;
    private final String content;
    private final IssueKeyword keyword;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final List<Stations> stations;
    private final String issueKey;
    private final Boolean processRange;
    private final Boolean lineInfoAvailable;
    private final String crawlingNo;
    @Builder
    public ExternalIssueCreate(String title, String content, IssueKeyword keyword, LocalDateTime startDate, LocalDateTime expireDate, List<Stations> stations, String issueKey, Boolean processRange, Boolean lineInfoAvailable, String crawlingNo) {
        this.title = title;
        this.content = content;
        this.keyword = keyword;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.stations = stations;
        this.issueKey = issueKey;
        this.processRange = processRange;
        this.lineInfoAvailable = lineInfoAvailable;
        this.crawlingNo = crawlingNo;
    }
    @Getter
    public static class Stations{
        private final String line;
        private final String name;
        @Builder
        public Stations(String line, String name) {
            this.line = line;
            this.name = name;
        }
    }
}
