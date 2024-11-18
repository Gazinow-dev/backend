package com.gazi.gazi_renew.member.service;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.domain.dto.RecentSearchCreate;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.FakeMemberRepository;
import com.gazi.gazi_renew.mock.FakeRecentSearchRepository;
import com.gazi.gazi_renew.mock.FakeSecurityUtil;
import com.gazi.gazi_renew.mock.TestClockHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RecentSearchServiceImplTest {
    private RecentSearchServiceImpl recentSearchServiceImpl;
    private FakeRecentSearchRepository fakeRecentSearchRepository;
    @BeforeEach
    void init() {
        fakeRecentSearchRepository = new FakeRecentSearchRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();

        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();


        this.recentSearchServiceImpl = new RecentSearchServiceImpl(fakeMemberRepository, fakeRecentSearchRepository,
                testClockHolder, fakeSecurityUtil);

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
        fakeMemberRepository.save(member1);
        fakeSecurityUtil.addEmail("mw310@naver.com");

        for (int i = 11; i <= 20; i++) {
            RecentSearch recentSearch = RecentSearch.builder()
                    .id((long) i)
                    .stationName("수도권 6호선" + i)
                    .stationLine("수도권 6호선")
                    .memberId(member1.getId())
                    .modifiedAt(LocalDateTime.parse("2024-04-21T00:09:"+i))
                    .build();
            fakeRecentSearchRepository.save(recentSearch);
        }
    }

    @Test
    void getRecentSearch는_최근_검색어를_조회할_수_있다() throws Exception{
        //when
        List<RecentSearch> recentSearchList = recentSearchServiceImpl.getRecentSearch();
        //then
        assertThat(recentSearchList.size()).isEqualTo(10);
        assertThat(recentSearchList.get(0).getId()).isEqualTo(20L);
        assertThat(recentSearchList.get(0).getStationName()).isEqualTo("수도권 6호선20");
        assertThat(recentSearchList.get(0).getStationLine()).isEqualTo("수도권 6호선");
    }
    @Test
    void addRecentSearch는_최근_검색어가_10개_이상이면_가장_오래된_검색어를_지운다() throws Exception{
        //given
        //미리 10개를 저장해 뒀음
        RecentSearchCreate recentSearchCreate = RecentSearchCreate.builder()
                .stationName("삼각지")
                .stationLine("수도권 6호선")
                .build();
        //when
        recentSearchServiceImpl.addRecentSearch(recentSearchCreate);
        //then
        List<RecentSearch> recentSearchList = fakeRecentSearchRepository.findAllByMemberOrderByModifiedAtDesc(1L);
        List<Long> recentSearchId = recentSearchList.stream()
                .map(RecentSearch::getId)
                .collect(Collectors.toList());

        assertThat(1L).isNotIn(recentSearchId); //1L은 지워져야 함
        assertThat(recentSearchList.size()).isEqualTo(10); //10를 넘으면 안된다
        assertThat(recentSearchList.get(0).getStationName()).isEqualTo("삼각지");
    }
    @Test
    void addRecentSearch는_같은_검색어를_입력할_경우에_검색_시간만_수정돼야_한다() throws Exception{
        //given
        //미리 10개를 저장해 뒀음
        RecentSearchCreate recentSearchCreate = RecentSearchCreate.builder()
                .stationName("수도권 6호선11")
                .stationLine("수도권 6호선")
                .build();
        Optional<RecentSearch> byIdAndMember = fakeRecentSearchRepository.findByIdAndMember(11L, 1L);
        LocalDateTime lastModifiedAt = byIdAndMember.get().getModifiedAt();
        //when
        recentSearchServiceImpl.addRecentSearch(recentSearchCreate);
        //then
        List<RecentSearch> recentSearchList = fakeRecentSearchRepository.findAllByMemberOrderByModifiedAtDesc(1L);

        assertThat(recentSearchList.get(0).getModifiedAt()).isNotEqualTo(lastModifiedAt); //시간만 수정되어야 함
        assertThat(recentSearchList.get(0).getStationName()).isEqualTo("수도권 6호선11");
    }
    @Test
    void recentDelete는_최근_검색어를_지울_수_있다() throws Exception{
        //given
        Long recentSearchID = 11L;
        //when
        recentSearchServiceImpl.recentDelete(recentSearchID);
        Optional<RecentSearch> result = fakeRecentSearchRepository.findByIdAndMember(recentSearchID, 1L);
        //then
        assertThat(result).isEmpty();
    }
}