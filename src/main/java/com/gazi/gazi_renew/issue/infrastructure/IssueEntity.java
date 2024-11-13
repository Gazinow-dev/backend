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
    private String content;
    private String line;
    private String crawlingNo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(updatable = false)
    @CreatedDate
    private LocalDate createdDate;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private IssueKeyword keyword;
    @Column(nullable = true)
    private Integer latestNo;
    @OneToMany(mappedBy = "issueEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikeEntity> likeEntities = new HashSet<>();
    // 다대다 관계 매핑
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_station",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private List<StationEntity> stationEntities;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_line",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "line_id")
    )
    private List<LineEntity> lineEntities;

    public static IssueEntity from(Issue issue) {
        IssueEntity issueEntity = new IssueEntity();
        issueEntity.crawlingNo = issue.getCrawlingNo();
        issueEntity.startDate = issue.getStartDate().withSecond(0).withNano(0);
        issueEntity.expireDate = issue.getExpireDate().withSecond(0).withNano(0);
        issueEntity.title = issue.getTitle();
        issueEntity.content = issue.getContent();
        issueEntity.stationEntities = issue.getStationList().stream()
                .map(StationEntity::from)
                .collect(Collectors.toList());
        issueEntity.lineEntities = issue.getLines().stream()
                .map(LineEntity::from)
                .collect(Collectors.toList());
        issueEntity.keyword = issue.getKeyword();
        issueEntity.latestNo = issue.getLatestNo();

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
                .lines(lineEntities.stream().map(LineEntity::toModel).collect(Collectors.toList()))
                .stationList(stationEntities.stream().map(StationEntity::toModel).collect(Collectors.toList()))
                .latestNo(latestNo)
                .likeCount(likeEntities.size())
                .build();
    }
}
