package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.domain.dto.RecentSearchCreate;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.TestClockHolder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class RecentSearchTest {
    @Test
    void RecentSearchCreate로_RecentSearch를_생성할_수_있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("temp")
                .build();

        RecentSearchCreate recentSearchCreate = RecentSearchCreate.builder()
                .stationName("삼각지")
                .stationLine("수도권 6호선")
                .build();
        LocalDateTime newTime = LocalDateTime.now();
        //when
        RecentSearch recentSearch = RecentSearch.from(recentSearchCreate, member.getId(), new TestClockHolder(newTime));

        //then
        assertThat(recentSearch.getStationName()).isEqualTo("삼각지");
        assertThat(recentSearch.getStationLine()).isEqualTo("수도권 6호선");
    }
}