package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;


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
    public InternalIssueCreate(String title, String content, IssueKeyword keyword, LocalDateTime startDate, LocalDateTime expireDate, List<String> lines, List<String> locations, String issueKey, Boolean processRange, Boolean lineInfoAvailable, String crawlingNo) {
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
