package com.gazi.gazi_renew.admin.domain;

import com.gazi.gazi_renew.mock.common.TestClockHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PenaltyTest {
    @Test
    void 사용자_제제는_초기_생성시에_현재_날짜보다_7일전으로_설정된다() throws Exception{
        //given
        Long memberId = 1L;
        LocalDateTime nowTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(nowTime);
        //when
        Penalty penalty = Penalty.from(memberId,testClockHolder);
        //then
        assertThat(penalty.getStartDate()).isEqualTo(nowTime.minusDays(7));
        assertThat(penalty.getExpireDate()).isEqualTo(nowTime.minusDays(7));
    }
    @Test
    void 입력으로_들어오는_기간만큼_사용자_제제_만료일자는_늘어나야한다() throws Exception{
        //given
        Long memberId = 1L;
        LocalDateTime nowTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(nowTime);
        int days = 7;
        //when
        Penalty penalty = Penalty.from(memberId,testClockHolder);
        penalty.extendPenalty(days, testClockHolder);
        //then
        assertThat(penalty.getExpireDate()).isEqualTo(nowTime.plusDays(days));
    }
}

