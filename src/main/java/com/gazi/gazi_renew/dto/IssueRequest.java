package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.domain.enums.SubwayDirection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class IssueRequest {

    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private String secretCode;
    private String crawlingNo;
    private IssueKeyword keyword;
    private List<String> lines;
    private List<Station> stations;
    private int latestNo;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Station{
        private String line;
        private int startStationCode;
        private int endStationCode;
        private IssueKeyword keyword;
        private SubwayDirection direction;
        // 생성자 추가
        public Station(String line, int startStationCode, int endStationCode) {
            this.line = line;
            this.startStationCode = startStationCode;
            this.endStationCode = endStationCode;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class updateContentDto{
        private Long id;
        private String content;
    }
}
