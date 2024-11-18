package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.station.infrastructure.LineEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "issue_line")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueLineEntity extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false, name = "issue_id")
    private IssueEntity issueEntity;
    @ManyToOne
    @JoinColumn(nullable = false, name = "line_id")
    private LineEntity lineEntity;

    public static IssueLineEntity from(IssueLine issueLine) {
        IssueLineEntity issueLineEntity = new IssueLineEntity();
        issueLineEntity.id = issueLine.getId();
        issueLineEntity.issueEntity = IssueEntity.from(issueLine.getIssue());
        issueLineEntity.lineEntity = LineEntity.from(issueLine.getLine());

        return issueLineEntity;
    }

    public IssueLine toModel() {
        return IssueLine.builder()
                .id(id)
                .issue(issueEntity.toModel())
                .line(lineEntity.toModel())
                .build();
    }

}
