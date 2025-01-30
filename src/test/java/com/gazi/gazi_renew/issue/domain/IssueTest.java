package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.ExternalIssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.InternalIssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.mock.common.TestClockHolder;
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
        Issue issue = Issue.from(issueCreate);
        //then
        assertThat(issue.getTitle()).isEqualTo("삼각지역 집회");
        assertThat(issue.getContent()).isEqualTo("삼각지역 집회 가는길 지금 이슈 테스트");
    }
    @Test
    void Issue_내용과_제목을_업데이트할_수_있다() throws Exception{
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
                .title("숙대입구역으로 변경")
                .content("가는길 지금")
                .build();
        Issue issue = Issue.from(issueCreate);
        //when
        Issue resultIssue = issue.update(issueUpdate);
        //then
        assertThat(resultIssue.getContent()).isEqualTo("가는길 지금");
        assertThat(resultIssue.getTitle()).isEqualTo("숙대입구역으로 변경");
    }
    @Test
    void 자동으로_등록하는_외부_이슈는_issueKey를_가지고_있다() throws Exception{
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ExternalIssueCreate.Stations stations = ExternalIssueCreate.Stations.builder()
                .name("수도권 6호선")
                .name("삼각지역")
                .build();
        ExternalIssueCreate externalIssueCreate = ExternalIssueCreate.builder()
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .stations(List.of(stations))
                .issueKey("20241115-시위-6호선")
                .build();
        //when
        Issue issue = Issue.fromExternalIssue(externalIssueCreate);
        //then
        assertThat(issue.getIssueKey()).isEqualTo("20241115-시위-6호선");
    }
    @Test
    void 자동으로_등록하는_내부_이슈는_issueKey를_가지고_있다() throws Exception{
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        InternalIssueCreate internalIssueCreate = InternalIssueCreate.builder()
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .lines(List.of("수도권 6호선"))
                .locations(List.of("삼각지역"))
                .issueKey("20241115-시위-6호선")
                .lineInfoAvailable(false)
                .processRange(false)
                .keyword(IssueKeyword.시위)
                .issueKey("20241115-시위-6호선")
                .build();
        //when
        Issue issue = Issue.fromInternalIssue(internalIssueCreate);
        //then
        assertThat(issue.getIssueKey()).isEqualTo("20241115-시위-6호선");
    }
    @Test
    void 이슈의_날짜를_수정할_수_있다() throws Exception{
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(now);
        Issue issue = Issue.builder()
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .issueKey("20241115-시위-6호선")
                .keyword(IssueKeyword.시위)
                .issueKey("20241115-시위-6호선")
                .build();
        //when
        issue = issue.updateDate(testClockHolder, now, now.plusMinutes(30));
        //then
        assertThat(issue.getStartDate()).isEqualTo(LocalDateTime.parse("2024-11-15 08:29:00", formatter));
        assertThat(issue.getExpireDate()).isEqualTo(now.plusMinutes(30));
    }
}