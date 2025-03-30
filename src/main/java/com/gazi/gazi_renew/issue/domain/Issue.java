package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.dto.ExternalIssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.InternalIssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.*;

import java.time.LocalDateTime;

@Getter
public class Issue {
    private final Long id;
    private final String title;
    private final String content;
    private final String issueKey;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final String secretCode;
    private final String crawlingNo;
    private final IssueKeyword keyword;
    private final int latestNo;
    private final int likeCount;
    private final int commentCount;
    @Builder
    public Issue(Long id, String title, String content, String issueKey, LocalDateTime startDate, LocalDateTime expireDate, String secretCode, String crawlingNo, IssueKeyword keyword, int latestNo, int likeCount, int commentCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.issueKey = issueKey;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.secretCode = secretCode;
        this.crawlingNo = crawlingNo;
        this.keyword = keyword;
        this.latestNo = latestNo;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    // 이슈 업데이트 (도메인 객체 행동 부여)
    public Issue update(IssueUpdate issueUpdate) {
        return Issue.builder()
                .id(this.id)
                .title(issueUpdate.getTitle())
                .content(issueUpdate.getContent()) // 변경된 content 반영
                .startDate(issueUpdate.getStartDate())
                .expireDate(issueUpdate.getExpireDate())
                .secretCode(this.secretCode)
                .crawlingNo(this.crawlingNo)
                .keyword(issueUpdate.getKeyword())
                .latestNo(this.latestNo)
                .likeCount(this.likeCount)
                .build();
    }

    public static Issue from(IssueCreate issueCreate) {
        return Issue.builder()
                .title(issueCreate.getTitle())
                .content(issueCreate.getContent())
                .startDate(issueCreate.getStartDate())
                .expireDate(issueCreate.getExpireDate())
                .secretCode(issueCreate.getSecretCode())
                .crawlingNo(issueCreate.getCrawlingNo())
                .keyword(issueCreate.getKeyword())
                .latestNo(issueCreate.getLatestNo())
                .build();
    }
    public static Issue fromExternalIssue(ExternalIssueCreate externalIssueCreate) {
        return Issue.builder()
                .title(externalIssueCreate.getTitle())
                .content(externalIssueCreate.getContent())
                .issueKey(externalIssueCreate.getIssueKey())
                .startDate(externalIssueCreate.getStartDate())
                .expireDate(externalIssueCreate.getExpireDate())
                .crawlingNo(externalIssueCreate.getCrawlingNo())
                .keyword(externalIssueCreate.getKeyword())
                .build();
    }
    public static Issue fromInternalIssue(InternalIssueCreate internalIssueCreate) {
        return Issue.builder()
                .title(internalIssueCreate.getTitle())
                .content(internalIssueCreate.getContent())
                .issueKey(internalIssueCreate.getIssueKey())
                .startDate(internalIssueCreate.getStartDate())
                .expireDate(internalIssueCreate.getExpireDate())
                .crawlingNo(internalIssueCreate.getCrawlingNo())
                .keyword(internalIssueCreate.getKeyword())
                .build();
    }

    public Issue incrementLikeCount() {
        return Issue.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content) // 변경된 content 반영
                .startDate(this.startDate)
                .expireDate(this.expireDate)
                .secretCode(this.secretCode)
                .crawlingNo(this.crawlingNo)
                .keyword(this.keyword)
                .latestNo(this.latestNo)
                .likeCount(this.likeCount + 1)
                .build();
    }

    public Issue decrementLikeCount() {
        int updateLikeCount = 0;
        if (this.likeCount > 0) {
            updateLikeCount = this.likeCount - 1;
        }
        return Issue.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content) // 변경된 content 반영
                .startDate(this.startDate)
                .expireDate(this.expireDate)
                .secretCode(this.secretCode)
                .crawlingNo(this.crawlingNo)
                .keyword(this.keyword)
                .latestNo(this.latestNo)
                .likeCount(updateLikeCount)
                .build();
    }
    public Issue updateDate(ClockHolder clockHolder, LocalDateTime startDate, LocalDateTime expireDate) {
        return Issue.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .startDate(this.startDate)
                .expireDate(expireDate)
                .secretCode(this.secretCode)
                .crawlingNo(this.crawlingNo)
                .keyword(this.keyword)
                .latestNo(this.latestNo)
                .likeCount(this.likeCount)
                .build();
    }
}
