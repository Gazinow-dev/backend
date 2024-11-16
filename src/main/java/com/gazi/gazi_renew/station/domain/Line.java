package com.gazi.gazi_renew.station.domain;

import com.gazi.gazi_renew.issue.domain.Issue;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class Line {
    private final Long id;
    private final String lineName;
    @Builder
    public Line(Long id, String lineName) {
        this.id = id;
        this.lineName = lineName;
    }
}
