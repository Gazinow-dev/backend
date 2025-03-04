package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.admin.service.port.MemberPenaltyRepository;
import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.common.FakeKafkaSender;
import com.gazi.gazi_renew.mock.common.FakeRedisUtilServiceImpl;
import com.gazi.gazi_renew.mock.issue.FakeIssueLineRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueStationRepository;
import com.gazi.gazi_renew.mock.member.FakeMemberRepository;
import com.gazi.gazi_renew.mock.station.FakeLineRepository;
import com.gazi.gazi_renew.mock.station.FakeSubwayRepository;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IssueManualServiceImplTest {
    private IssueManualServiceImpl issueManualServiceImpl;
    private FakeIssueRepository fakeIssueRepository;
    private FakeIssueStationRepository fakeIssueStationRepository;
    private FakeIssueLineRepository fakeIssueLineRepository;
    @BeforeEach
    void init() {
        ObjectMapper mapper = new ObjectMapper();

        fakeIssueRepository = new FakeIssueRepository();
        FakeSubwayRepository fakeSubwayRepository = new FakeSubwayRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakeLineRepository fakeLineRepository = new FakeLineRepository();

        FakeRedisUtilServiceImpl fakeRedisUtilService = new FakeRedisUtilServiceImpl(mapper);
        fakeIssueStationRepository = new FakeIssueStationRepository();
        fakeIssueLineRepository = new FakeIssueLineRepository();
        FakeKafkaSender fakeKafkaSender = new FakeKafkaSender();

        this.issueManualServiceImpl = new IssueManualServiceImpl(fakeIssueRepository, fakeSubwayRepository
                , fakeLineRepository, fakeIssueLineRepository, fakeIssueStationRepository, fakeRedisUtilService, fakeKafkaSender);

        Line line = Line.builder()
                .id(1L)
                .lineName("수도권 6호선")
                .build();
        Line line1 = Line.builder()
                .id(2L)
                .lineName("수도권 1호선")
                .build();
        fakeLineRepository.save(line);
        fakeLineRepository.save(line1);

        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();

        Member member1 = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("encoded_tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(false)
                .mySavedRouteNotificationEnabled(false)
                .firebaseToken("firebaseToken")
                .build();

        fakeMemberRepository.save(member1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Issue issue = Issue.builder()
                .id(1L)
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .crawlingNo("1")
                .likeCount(10)
                .build();
        Issue issue2 = Issue.builder()
                .id(2L)
                .title("서울역 사고")
                .content("서울역 사고 테스트")
                .startDate(LocalDateTime.parse("2024-11-16 08:00:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-16 10:00:00", formatter))
                .keyword(IssueKeyword.사고)
                .crawlingNo("3")
                .likeCount(5)
                .build();

        fakeIssueRepository.save(issue);
        fakeIssueRepository.save(issue2);

        IssueStation issueStation = IssueStation.builder()
                .issue(issue)
                .station(station1)
                .build();

        fakeIssueStationRepository.save(issueStation);
        IssueLine issueLine = IssueLine.builder()
                .issue(issue)
                .line(line)
                .build();
        IssueLine issueLine1 = IssueLine.builder()
                .issue(issue)
                .line(line1)
                .build();
        IssueLine issueLine2 = IssueLine.builder()
                .issue(issue2)
                .line(line1)
                .build(); //1호선 2개 저장

        fakeIssueLineRepository.save(issueLine);
        fakeIssueLineRepository.save(issueLine1);
        fakeIssueLineRepository.save(issueLine2);

        // 2호선 역 데이터 (순환선)
        for (int i = 201; i <= 243; i++) {
            Station station = Station.builder()
                    .id((long) i)
                    .line("수도권 2호선")
                    .name("2호선 역" + i)
                    .issueStationCode(i)
                    .build();
            fakeSubwayRepository.save(station);
        }

        // 1호선 역 데이터 (직선)
        for (int i = 11; i <= 15; i++) {
            Station station = Station.builder()
                    .id((long) i)
                    .line("수도권 1호선")
                    .name("1호선 역" + (i - 10))
                    .issueStationCode(i)
                    .build();
            fakeSubwayRepository.save(station);
        }

        ReflectionTestUtils.setField(issueManualServiceImpl, "secretCode", "gazichiki12!@");
    }
    @Test
    void Issue는_addIssue를_통해_추가할_수_있다() throws Exception{
        //given
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
                .secretCode("gazichiki12!@")
                .lines(Arrays.asList("수도권 6호선"))
                .stations(Arrays.asList(station))
                .crawlingNo("2")
                .latestNo(2)
                .build();
        //when
        boolean result = issueManualServiceImpl.addIssue(issueCreate);
        //then
        assertThat(result).isTrue();
    }
    @Test
    void addIssue는_이슈_등록_시_비밀코드가_틀릴_경우에_에러를_터트린다() throws Exception{
        //given
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
                .secretCode("wrongCode")
                .lines(Arrays.asList("수도권 6호선"))
                .stations(Arrays.asList(station))
                .crawlingNo("2")
                .latestNo(2)
                .build();
        //when
        assertThrows(CustomException.class, () -> issueManualServiceImpl.addIssue(issueCreate));
    }
    @Test
    void addIssue는_이슈_등록_시_중복된_crawlingNo을_사용할_경우에_에러를_터트린다() throws Exception{
        //given
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
                .secretCode("gazichiki12!@")
                .lines(Arrays.asList("수도권 6호선"))
                .stations(Arrays.asList(station))
                .crawlingNo("1") //중복
                .latestNo(2)
                .build();
        //when
        assertThrows(CustomException.class, () -> issueManualServiceImpl.addIssue(issueCreate));
    }

    @Test
    void getStationList는_2호선과_다른노선을_구분하여_올바르게_처리한다() {
        // given
        List<IssueCreate.Station> issueStations = Arrays.asList(
                IssueCreate.Station.builder()
                        .line("수도권 2호선")
                        .startStationCode(203)
                        .endStationCode(242)
                        .direction(SubwayDirection.Counterclockwise) // 반시계 방향
                        .build(),
                IssueCreate.Station.builder()
                        .line("수도권 1호선")
                        .startStationCode(12)
                        .endStationCode(14)
                        .build()
        );

        // when
        List<Station> stations = issueManualServiceImpl.getStationList(issueStations);

        // then
        assertThat(stations).hasSize(8); // 2호선 5개 + 1호선 3개
        assertThat(stations).extracting(Station::getLine)
                .contains("수도권 2호선", "수도권 1호선");
    }

    @Test
    void handleClockwiseDirection는_순환선에서_내선일때_시작역_코드가_끝역_코드보다_작아야_연속적인_구간을_처리한다() {
        // given
        int startIssueStationCode = 201; // 출발역
        int endIssueStationCode = 205;   // 도착역

        // when
        List<Station> stations = issueManualServiceImpl.handleClockwiseDirection(startIssueStationCode, endIssueStationCode);

        // then
        assertThat(stations).hasSize(5); // 역 201, 202, 203, 204, 205
        assertThat(stations).extracting(Station::getIssueStationCode)
                .containsExactly(201, 202, 203, 204, 205);
    }
    @Test
    void handleCounterClockwiseDirection는_순환선에서_외선일때_시작역_코드가_끝역_코드보다_커야_연속적인_구간을_처리한다() {
        // given
        int startIssueStationCode = 205; // 출발역
        int endIssueStationCode = 201;   // 도착역

        // when
        List<Station> stations = issueManualServiceImpl.handleCounterClockwiseDirection(startIssueStationCode, endIssueStationCode);

        // then
        assertThat(stations).hasSize(5); // 역 201, 202, 203, 204, 205
        assertThat(stations).extracting(Station::getIssueStationCode)
                .containsExactly(201, 202, 203, 204, 205);
    }
    @Test
    void handleCounterClockwiseDirection는_순환선에서_외선일때_시작역_코드가_끝역_코드보다_작으면_원형을_넘는_구간을_처리한다() {
        // given
        int startIssueStationCode = 203; // 출발역
        int endIssueStationCode = 242;   // 도착역

        // when
        List<Station> stations = issueManualServiceImpl.handleCounterClockwiseDirection(startIssueStationCode, endIssueStationCode);

        // then
        assertThat(stations).hasSize(5); // 역 242, 243, 201, 202, 203
        assertThat(stations).extracting(Station::getIssueStationCode)
                .containsExactly(242, 243, 201, 202, 203);
    }
    @Test
    void getStationsForCircularRoute는_원형을_넘어가는_구간을_처리한다() {
        // given
        int startIssueStationCode = 241; // 출발역
        int endIssueStationCode = 203;   // 도착역

        // when
        List<Station> stations = issueManualServiceImpl.getStationsForCircularRoute(startIssueStationCode, endIssueStationCode);

        // then
        assertThat(stations).hasSize(6); // 역 241, 242, 243, 201, 202, 203
        assertThat(stations).extracting(Station::getIssueStationCode)
                .containsExactly(241, 242, 243, 201, 202, 203);
    }
    @Test
    void findStationsForOtherLines는_2호선이_아닌_직선구간의_역들을_조회한다() {
        // given
        int startIssueStationCode = 12; // 출발역
        int endIssueStationCode = 14;   // 도착역

        // when
        List<Station> stations = issueManualServiceImpl.findStationsForOtherLines(startIssueStationCode, endIssueStationCode);

        // then
        assertThat(stations).hasSize(3); // 역 12, 13, 14
        assertThat(stations).extracting(Station::getIssueStationCode)
                .containsExactly(12, 13, 14);
    }
}