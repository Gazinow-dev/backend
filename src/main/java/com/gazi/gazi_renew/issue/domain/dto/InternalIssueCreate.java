package com.gazi.gazi_renew.issue.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Getter
public class InternalIssueCreate {
    private final String title;
    private final String content;
    private final IssueKeyword keyword;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final List<String> lines;
    private final List<String> locations;
    private final String issueKey;
    private final Boolean processRange;
    private final Boolean lineInfoAvailable;
    private final String crawlingNo;
    @Builder
    public InternalIssueCreate( @JsonProperty("title") String title,
                                @JsonProperty("content") String content,
                                @JsonProperty("keyword") IssueKeyword keyword,
                                @JsonProperty("startDate") LocalDateTime startDate,
                                @JsonProperty("expireDate") LocalDateTime expireDate,
                                @JsonProperty("lines") List<String> lines,
                                @JsonProperty("locations") List<String> locations,
                                @JsonProperty("issue_key") String issueKey,
                                @JsonProperty("processRange") Boolean processRange,
                                @JsonProperty("lineInfoAvailable") Boolean lineInfoAvailable,
                                @JsonProperty("crawlingNo") String crawlingNo) {
        this.title = title;
        this.content = content;
        this.keyword = keyword;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.lines = lines;
        this.locations = locations;
        this.issueKey = issueKey;
        this.processRange = processRange;
        this.lineInfoAvailable = lineInfoAvailable;
        this.crawlingNo = crawlingNo;
    }
}
