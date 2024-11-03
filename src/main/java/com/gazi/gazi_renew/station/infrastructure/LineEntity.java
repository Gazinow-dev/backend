package com.gazi.gazi_renew.station.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.station.domain.Line;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line_table")
@Entity
public class LineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lineName;
    @JsonIgnore // issues 필드를 JSON 시리얼라이제이션에서 무시
    @ManyToMany(mappedBy = "lines" ,fetch = FetchType.LAZY)
    private List<IssueEntity> issueEntities;

    public static LineEntity from(Line line) {
        LineEntity lineEntity = new LineEntity();
        lineEntity.id = line.getId();
        lineEntity.lineName = line.getLineName();
        lineEntity.issueEntities = line.getIssueList().stream()
                .map(IssueEntity::from)
                .collect(Collectors.toList());

        return lineEntity;
    }
}
