package com.gazi.gazi_renew.domain;


import com.gazi.gazi_renew.domain.enums.IssueKeyword;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue")
@Entity
public class Issue extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
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
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();
    // 다대다 관계 매핑
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_station",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private List<Station> stations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_line",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "line_id")
    )
    private List<Line> lines;
    // todo: 어디까지 체크되었는지를 가져오기위한 최근조회 num 추가

}
