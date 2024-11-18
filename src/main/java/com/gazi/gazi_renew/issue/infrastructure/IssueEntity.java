package com.gazi.gazi_renew.issue.infrastructure;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.infrastructure.LineEntity;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue")
@Entity
public class IssueEntity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
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
