package com.gazi.gazi_renew.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @Setter
    public static class Station{
        private String line;
        private int startStationCode;
        private int endStationCode;

        // 기본 생성자 추가
        public Station() {
        }

        // 생성자 추가
        public Station(String line, int startStationCode, int endStationCode) {
            this.line = line;
            this.startStationCode = startStationCode;
            this.endStationCode = endStationCode;
        }
    }
}
