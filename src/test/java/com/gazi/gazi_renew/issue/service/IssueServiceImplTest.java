package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.common.FakeKafkaSender;
import com.gazi.gazi_renew.mock.common.FakeLikeRepository;
import com.gazi.gazi_renew.mock.common.FakeRedisUtilServiceImpl;
import com.gazi.gazi_renew.mock.common.FakeSecurityUtil;
import com.gazi.gazi_renew.mock.issue.FakeIssueLineRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueStationRepository;
import com.gazi.gazi_renew.mock.member.FakeMemberRepository;
import com.gazi.gazi_renew.mock.station.FakeLineRepository;
import com.gazi.gazi_renew.mock.station.FakeSubwayRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IssueServiceImplTest {
    private IssueServiceImpl issueServiceImpl;
    private FakeIssueRepository fakeIssueRepository;
    private FakeIssueStationRepository fakeIssueStationRepository;
    private FakeIssueLineRepository fakeIssueLineRepository;
    @BeforeEach
    void init() {
        ObjectMapper mapper = new ObjectMapper();

        FakeLikeRepository fakeLikeRepository = new FakeLikeRepository();
        fakeIssueRepository = new FakeIssueRepository();
        FakeSubwayRepository fakeSubwayRepository = new FakeSubwayRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakeLineRepository fakeLineRepository = new FakeLineRepository();

        FakeRedisUtilServiceImpl fakeRedisUtilService = new FakeRedisUtilServiceImpl(mapper);
        fakeIssueStationRepository = new FakeIssueStationRepository();
        fakeIssueLineRepository = new FakeIssueLineRepository();
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();
        FakeKafkaSender fakeKafkaSender = new FakeKafkaSender();

        this.issueServiceImpl = new IssueServiceImpl(fakeIssueRepository, fakeLikeRepository, fakeSubwayRepository, fakeMemberRepository
                , fakeLineRepository, fakeRedisUtilService, fakeSecurityUtil, fakeIssueLineRepository, fakeIssueStationRepository, fakeKafkaSender);

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
        fakeSecurityUtil.addEmail("mw310@naver.com");

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
        Like like = Like.builder()
                .id(1L)
                .memberId(member1.getId())
                .issueId(issue.getId())
                .build();
        fakeLikeRepository.save(like);

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

        ReflectionTestUtils.setField(issueServiceImpl, "secretCode", "gazichiki12!@");
    }
    @Test
    void getIssue는_id를_통해_조회가_가능하다() throws Exception{
        //given
        Long id = 1L;
        //when
        IssueStationDetail issue = issueServiceImpl.getIssue(id);
        //then
        assertThat(issue.getIssue().getId()).isEqualTo(1L);
        assertThat(issue.getIssue().getTitle()).isEqualTo("삼각지역 집회");
        assertThat(issue.getIssue().getContent()).isEqualTo("삼각지역 집회 가는길 지금 이슈 테스트");
    }
    @Test
    void getIssue는_멤버가_누른_좋아요를_조회가_가능하다() throws Exception{
        //given
        Long id = 1L;
        //when
        IssueStationDetail issue = issueServiceImpl.getIssue(id);
        //then
        assertThat(issue.isLike()).isTrue();
    }
    @Test
    void getIssue는_잘못된_id가_들어올_경우에_에러를_터트린다() throws Exception{
        //given
        Long id = 111L;
        //when
        assertThrows(EntityNotFoundException.class , () -> {
            issueServiceImpl.getIssue(id);
        });
    }
    @Test
    void getIssues는_이슈들을_조회할_수_있다() throws Exception{
        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<IssueStationDetail> issuePage = issueServiceImpl.getIssues(pageable);

        // then
        assertThat(issuePage).isNotNull();
        assertThat(issuePage.getTotalElements()).isEqualTo(2);
    }
    @Test
    void getLineByIssues는_호선별로_이슈를_조회할_수_있다() throws Exception {
        // given
        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<IssueStationDetail> issuePage = issueServiceImpl.getLineByIssues("수도권 1호선", pageable);

        // then
        assertThat(issuePage).isNotNull();
        assertThat(issuePage.getTotalElements()).isEqualTo(2);
        assertThat(issuePage.getContent()).extracting(IssueStationDetail::getIssue).extracting(Issue::getTitle)
                .containsExactly("서울역 사고", "삼각지역 집회");  //정렬 순서도 체크
    }
    @Test
    void getLineByIssues는_존재하지_않는_호선을_조회하면_에러를_터트린다() throws Exception {
        // given
        String invalidLineName = "수도권 99호선";
        Pageable pageable = PageRequest.of(0, 2);

        // when & then
        assertThrows(EntityNotFoundException.class,
                () -> issueServiceImpl.getLineByIssues(invalidLineName, pageable));
    }
    @Test
    void getPopularIssues는_좋아요_숫자가_5개_이상인_이슈를_조회할_수_있다() throws Exception{
        //given
        //when
        List<IssueStationDetail> popularIssues = issueServiceImpl.getPopularIssues();
        //then
        assertThat(popularIssues.size()).isEqualTo(2);
        assertThat(popularIssues.get(0).getIssue().getTitle()).isEqualTo("삼각지역 집회");
        assertThat(popularIssues.get(0).getIssue().getContent()).isEqualTo("삼각지역 집회 가는길 지금 이슈 테스트");
        assertThat(popularIssues.get(1).getIssue().getTitle()).isEqualTo("서울역 사고");
        assertThat(popularIssues.get(1).getIssue().getContent()).isEqualTo("서울역 사고 테스트");
    }
    @Test
    void updateIssue는_Issue_내용과_제목을_업데이트할_수_있다() throws Exception{
        //given
        IssueUpdate issueUpdate = IssueUpdate.builder()
                .title("updateTest")
                .id(1L)
                .content("삼각지에서 효창공원 시위로 변경")
                .build();
        //when
        issueServiceImpl.updateIssue(issueUpdate);
        //then
        Optional<Issue> result = fakeIssueRepository.findById(1L);
        assertThat(result.get().getTitle()).isEqualTo("updateTest");
        assertThat(result.get().getContent()).isEqualTo("삼각지에서 효창공원 시위로 변경");
        assertThat(result.get().getId()).isEqualTo(1L);
    }
}