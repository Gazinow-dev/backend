package com.gazi.gazi_renew.admin.domain.dto;

import com.gazi.gazi_renew.mock.common.TestClockHolder;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AiFilterReportTest {
    @Test
    void topis_모든_공지사항을_저장한다() throws Exception{
        //given
        AiFilterReportCreate aiFilterReportCreate = AiFilterReportCreate.builder()
                .issueTitle("2월 첫째주 topis 이슈 현황")
                .issueContent("2월 첫째주 topis 이슈 현황입니다")
                .aiFiltered(true)
                .registered(true)
                .failureReason(null)
                .build();
        LocalDateTime newTime = LocalDateTime.now();
        //when
        AiFilterReport aiFilterReport = AiFilterReport.from(aiFilterReportCreate, new TestClockHolder(newTime));
        //then
        assertThat(aiFilterReport.getIssueTitle()).isEqualTo("2월 첫째주 topis 이슈 현황");
        assertThat(aiFilterReport.getIssueContent()).isEqualTo("2월 첫째주 topis 이슈 현황입니다");
        assertThat(aiFilterReport.getCreatedAt()).isEqualTo(newTime);
    }

}