package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class IssueResponse {

    private Long id;
    private String title;
    private String content;
    private String date;
    private String line;
    private int likeCount;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private List<StationDto> stationDtos;

    public static StationDto getStation(Station station){
        StationDto stationDto = new StationDto();
        stationDto.line = station.getLine();
        stationDto.stationName = station.getName();
        return stationDto;
    }

    public static List<StationDto> getStations(List<Station> stations){
        List<StationDto> stationDtos = (List<StationDto>) stations.stream().map(
                m -> getStation(m)
        );
        return stationDtos;
    }

    public static class StationDto {
        private String line;
        private String stationName;
    }

    // 이슈 요약
    @Builder
    public static class IssueSummaryDto{
        private Long id;
        private String title;
        private int likeCount;
    }
}
