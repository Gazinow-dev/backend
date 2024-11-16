package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueTest {
    @Test
    void Issue는_IssueCreate를_통해_Issue_티켓을_생성할_수_있다() throws Exception{
        //given
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("삼각지")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(2)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(2)
                .build();
        List<Station> stationList = Arrays.asList(station1, station2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        IssueCreate.Station station = IssueCreate.Station
                .builder()
                .line("수도권 6호선")
                .startStationCode(1)
                .endStationCode(2)
                .keyword(IssueKeyword.시위)
                .build();
        IssueCreate issueCreate = IssueCreate.builder()
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .lines(Arrays.asList("수도권 6호선"))
                .stations(Arrays.asList(station))
                .latestNo(2)
                .build();

        //when
        Issue issue = Issue.from(issueCreate, stationList);
        //then
        assertThat(issue.getTitle()).isEqualTo("삼각지역 집회");
        assertThat(issue.getContent()).isEqualTo("삼각지역 집회 가는길 지금 이슈 테스트");
        assertThat(issue.getStationList().get(0).getName()).isEqualTo("삼각지");
        assertThat(issue.getStationList().get(1).getName()).isEqualTo("효창공원앞");
    }
    @Test
    void Issue_내용을_업데이트할_수_있다() throws Exception{
        //given
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("삼각지")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(2)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(2)
                .build();
        List<Station> stationList = Arrays.asList(station1, station2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        IssueCreate.Station station = IssueCreate.Station
                .builder()
                .line("수도권 6호선")
                .startStationCode(1)
                .endStationCode(2)
                .keyword(IssueKeyword.시위)
                .build();
        IssueCreate issueCreate = IssueCreate.builder()
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .lines(Arrays.asList("수도권 6호선"))
                .stations(Arrays.asList(station))
                .latestNo(2)
                .build();
        IssueUpdate issueUpdate = IssueUpdate.builder()
                .content("가는길 지금")
                .build();
        Issue issue = Issue.from(issueCreate, stationList);
        //when
        Issue resultIssue = issue.update(issueUpdate);
        //then
        assertThat(resultIssue.getContent()).isEqualTo("가는길 지금");
        //다른 값은 달라지면 안됨
        assertThat(issue.getTitle()).isEqualTo("삼각지역 집회");
        assertThat(resultIssue.getStationList().get(0).getName()).isEqualTo("삼각지");
        assertThat(resultIssue.getStationList().get(1).getName()).isEqualTo("효창공원앞");

    }
}