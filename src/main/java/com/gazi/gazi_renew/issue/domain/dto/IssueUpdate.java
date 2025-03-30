package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "이슈 수정 요청 DTO")
public class IssueUpdate {

    @Schema(description = "이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "이슈 제목", example = "지하철 운행 중단")
    private final String title;

    @Schema(description = "이슈 내용", example = "강풍으로 인해 일부 구간 운행이 중단되었습니다.")
    private final String content;

    @Schema(description = "이슈 시작 날짜", example = "2025-03-30T06:22:03.984Z")
    private final LocalDateTime startDate;

    @Schema(description = "이슈 만료 날짜", example = "2025-03-31T06:22:03.984Z")
    private final LocalDateTime expireDate;

    @Schema(description = "이슈 키워드", example = "자연재해")
    private final IssueKeyword keyword;

    @Schema(description = "이슈에 해당하는 역 목록")
    private final List<IssueUpdateStation> issueUpdateStationList;

    @Builder
    public IssueUpdate(Long id, String title, String content, LocalDateTime startDate, LocalDateTime expireDate, IssueKeyword keyword, List<IssueUpdateStation> issueUpdateStationList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.keyword = keyword;
        this.issueUpdateStationList = issueUpdateStationList;
    }

    @Getter
    @Schema(description = "이슈가 발생한 역 정보")
    public static class IssueUpdateStation {

        @Schema(description = "노선 이름", example = "2호선")
        private final String line;

        @Schema(description = "시작 역 코드", example = "1001")
        private final int startStationCode;

        @Schema(description = "종료 역 코드", example = "1005")
        private final int endStationCode;

        @Schema(description = "방향", example = "Clockwise, Counterclockwise,Sinjeong,Seongsu")
        private final SubwayDirection direction;

        @Builder
        public IssueUpdateStation(String line, int startStationCode, int endStationCode, SubwayDirection direction) {
            this.line = line;
            this.startStationCode = startStationCode;
            this.endStationCode = endStationCode;
            this.direction = direction;
        }
    }
}
