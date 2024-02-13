package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class IssueRequest {

    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private String secretCode;
    private String crawlingNo;
    private List<Station> stations;

    @Builder
    @Getter
    public static class Station{
        private String line;
        private int startStationCode;
        private int EndStationCode;
    }
}
