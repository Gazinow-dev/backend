package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.issue.domain.dto.LikeCreate;
import com.gazi.gazi_renew.issue.domain.dto.LikeDelete;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.FakeIssueRepository;
import com.gazi.gazi_renew.mock.FakeLikeRepository;
import com.gazi.gazi_renew.mock.FakeMemberRepository;
import com.gazi.gazi_renew.mock.FakeSecurityUtil;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LikeServiceImplTest {
    private LikeServiceImpl likeServiceImpl;
    private FakeLikeRepository fakeLikeRepository;
    @BeforeEach
    void init() {
        FakeIssueRepository fakeIssueRepository = new FakeIssueRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        fakeLikeRepository = new FakeLikeRepository();

        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();

        this.likeServiceImpl = new LikeServiceImpl(fakeIssueRepository, fakeLikeRepository,
                fakeMemberRepository, fakeSecurityUtil);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Line line1 = Line.builder()
                .id(1L)
                .lineName("수도권 1호선")
                .build();

        Station station2 = Station.builder()
                .id(1L)
                .line("수도권 1호선")
                .name("서울역")
                .stationCode(2)
                .lat(37.556706)
                .lng(126.972322)
                .issueStationCode(2)
                .build();
        Member member1 = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("encoded_tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(false)
                .mySavedRouteNotificationEnabled(false)
                .routeDetailNotificationEnabled(false)
                .firebaseToken("firebaseToken")
                .build();
        fakeSecurityUtil.addEmail("mw310@naver.com");

        fakeMemberRepository.save(member1);

        Issue issue1 = Issue.builder()
                .id(1L)
                .title("서울역 대규모 행사")
                .content("서울역 대규모 행사 테스트")
                .startDate(LocalDateTime.parse("2024-11-16 09:00:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-16 18:00:00", formatter))
                .keyword(IssueKeyword.행사)
                .stationList(Arrays.asList(station2))
                .lines(Arrays.asList(line1))
                .crawlingNo("2")
                .likeCount(10)
                .build();
        Issue issue2 = Issue.builder()
                .id(2L)
                .title("명동역 대규모 행사")
                .content("명동역 대규모 행사 테스트")
                .startDate(LocalDateTime.parse("2024-11-16 09:00:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-16 18:00:00", formatter))
                .keyword(IssueKeyword.행사)
                .stationList(Arrays.asList(station2))
                .lines(Arrays.asList(line1))
                .crawlingNo("2")
                .likeCount(10)
                .build();


        fakeIssueRepository.save(issue1);
        fakeIssueRepository.save(issue2);

        Like like = Like.builder()
                .id(1L)
                .issue(issue1)
                .memberId(1L)
                .build();

        fakeLikeRepository.save(like);
    }
    @Test
    void likeIssue는_이슈의_좋아요를_저장할_수_있다() throws Exception{
        //given
        LikeCreate likeCreate = LikeCreate.builder()
                .issueId(2L)
                .build();

        //when
        Long likeId = likeServiceImpl.likeIssue(likeCreate);
        //then
        fakeLikeRepository.findByIssueAndMember(1L, 1L);
        assertThat(likeId).isEqualTo(2L);
    }
    @Test
    void likeIssue는_한_멤버가_이미_누른_이슈의_좋아요를_저장할_수_없다() throws Exception{
        //given
        LikeCreate likeCreate = LikeCreate.builder()
                .issueId(1L)
                .build();
        //when
        assertThrows(CustomException.class, () -> {
            likeServiceImpl.likeIssue(likeCreate);
        });
    }
    @Test
    void deleteLikeIssue는_좋아요를_삭제할_수_있다() throws Exception{
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Issue issue1 = Issue.builder()
                .id(2L)
                .title("서울역 대규모 행사")
                .content("서울역 대규모 행사 테스트")
                .startDate(LocalDateTime.parse("2024-11-16 09:00:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-16 18:00:00", formatter))
                .keyword(IssueKeyword.행사)
                .crawlingNo("2")
                .likeCount(10)
                .build();
        Like like = Like.builder()
                .id(2L)
                .memberId(1L)
                .issue(issue1)
                .build();
        fakeLikeRepository.save(like);

        LikeDelete likeDelete = LikeDelete.builder()
                .issueId(2L)
                .build();
        //when
        likeServiceImpl.deleteLikeIssue(likeDelete);
        //then
        Optional<Like> result = fakeLikeRepository.findByIssueAndMember(2L, 1L);

        assertThat(result).isEmpty();
    }
}

