package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import com.gazi.gazi_renew.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class MyFindRoadStationTest {
    @Test
    void MyFindRoadStation은_MyFindRoadStationCreate_객체로_생성할_수_있다() throws Exception{
        //given
        MyFindRoadStationCreate myFindRoadStationCreate = MyFindRoadStationCreate.builder()
                .index(1)
                .stationName("삼각지")
                .stationCode(1)
                .build();
        //when
        MyFindRoadStation myFindRoadStation = MyFindRoadStation.from(myFindRoadStationCreate, 1L);
        //then
        assertThat(myFindRoadStation.getStationName()).isEqualTo("삼각지");
        assertThat(myFindRoadStation.getIndex()).isEqualTo(1);
    }
    @Test
    void MyFindRoadStation는_지하철역들의_이슈를_업데이트할_수_있다() throws Exception{
        //given
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Issue issue = Issue.builder()
                .id(1L)
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .likeCount(0)
                .build();

        MyFindRoadStation myFindRoadStation = MyFindRoadStation.builder()
                .id(1L)
                .index(1)
                .stationName("삼각지")
                .myFindRoadSubPathId(1L)
                .build();
        //when
        MyFindRoadStation result = myFindRoadStation.updateIssueList(Arrays.asList(issue));
        //then
        assertThat(result.getIssueList().size()).isEqualTo(1);
        assertThat(result.getIssueList().get(0).getTitle()).isEqualTo("삼각지역 집회");
        assertThat(result.getIssueList().get(0).getContent()).isEqualTo("삼각지역 집회 가는길 지금 이슈 테스트");
    }

}