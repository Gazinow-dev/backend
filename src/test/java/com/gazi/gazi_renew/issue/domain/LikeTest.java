package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.LikeCreate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LikeTest {
    @Test
    void LikeCreate를_통해_Like를_생성할_수_있다() throws Exception{
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Line line1 = Line.builder()
                .id(2L)
                .lineName("수도권 1호선")
                .build();

        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 1호선")
                .name("서울역")
                .stationCode(2)
                .lat(37.556706)
                .lng(126.972322)
                .issueStationCode(2)
                .build();

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


        Long memberId = 1L;

        LikeCreate likeCreate = LikeCreate.builder()
                .issueId(1L)
                .build();
        //when
        Like like = Like.from(likeCreate, memberId, issue1);
        //then
        assertThat(like.getIssue().getId()).isEqualTo(1L);
        assertThat(like.getIssue().getTitle()).isEqualTo("서울역 대규모 행사");
    }

}