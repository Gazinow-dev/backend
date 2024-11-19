package com.gazi.gazi_renew.station.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.station.domain.Line;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line_table")
@Entity
public class LineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lineName;
    public static LineEntity from(Line line) {
        LineEntity lineEntity = new LineEntity();
        lineEntity.id = line.getId();
        lineEntity.lineName = line.getLineName();

        return lineEntity;
    }
    public Line toModel() {
        return Line.builder()
                .id(id)
                .lineName(lineName)
                .build();
    }
}
