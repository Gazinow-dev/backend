package com.gazi.gazi_renew.issue.infrastructure.entity;


import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue")
@Entity
public class IssueEntity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String line;
    private String crawlingNo;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private IssueKeyword keyword;
    private Integer latestNo;
    @Column(nullable = false)
    private int likeCount;

    public static IssueEntity from(Issue issue) {
        IssueEntity issueEntity = new IssueEntity();
        issueEntity.id = issue.getId();
        issueEntity.crawlingNo = issue.getCrawlingNo();
        issueEntity.startDate = issue.getStartDate().withSecond(0).withNano(0);
        issueEntity.expireDate = issue.getExpireDate().withSecond(0).withNano(0);
        issueEntity.title = issue.getTitle();
        issueEntity.content = issue.getContent();
        issueEntity.keyword = issue.getKeyword();
        issueEntity.latestNo = issue.getLatestNo();
        issueEntity.likeCount = issue.getLikeCount();

        return issueEntity;
    }
    public Issue toModel(){
        return Issue.builder()
                .id(id)
                .title(title)
                .content(content)
                .startDate(startDate)
                .expireDate(expireDate)
                .crawlingNo(crawlingNo)
                .keyword(keyword)
                .latestNo(latestNo)
                .likeCount(likeCount)
                .build();
    }
}
