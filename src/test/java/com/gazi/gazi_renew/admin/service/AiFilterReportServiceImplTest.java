package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import com.gazi.gazi_renew.admin.domain.dto.AiFilterReportCreate;
import com.gazi.gazi_renew.mock.admin.FakeAiFilterReportRepository;
import com.gazi.gazi_renew.mock.common.TestClockHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AiFilterReportServiceImplTest {
    private AiFilterReportServiceImpl aiFilterReportService;

    @BeforeEach
    void init() {
        FakeAiFilterReportRepository fakeAiFilterReportRepository = new FakeAiFilterReportRepository();
        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);

        this.aiFilterReportService = new AiFilterReportServiceImpl(fakeAiFilterReportRepository, testClockHolder);
        AiFilterReport aiFilterReport = AiFilterReport.builder()
                .id(1L)
                .issueTitle("2월 첫째주 topis 이슈 현황")
                .issueContent("2월 첫째주 topis 이슈 현황입니다")
                .aiFiltered(true)
                .registered(true)
                .failureReason(null)
                .createdAt(newTime.minusDays(6))
                .build();
        AiFilterReport aiFilterReport2 = AiFilterReport.builder()
                .id(2L)
                .issueTitle("2월 첫째주 topis 이슈 현황2")
                .issueContent("2월 첫째주 topis 이슈 현황2입니다")
                .aiFiltered(true)
                .registered(true)
                .failureReason(null)
                .createdAt(newTime.minusDays(5))
                .build();
        AiFilterReport aiFilterReport3 = AiFilterReport.builder()
                .id(3L)
                .issueTitle("1월 첫째주 topis 이슈 현황2")
                .issueContent("1월 첫째주 topis 이슈 현황2입니다")
                .aiFiltered(true)
                .registered(true)
                .failureReason(null)
                .createdAt(newTime.minusDays(31))
                .build();
        fakeAiFilterReportRepository.save(aiFilterReport);
        fakeAiFilterReportRepository.save(aiFilterReport2);
        fakeAiFilterReportRepository.save(aiFilterReport3);
    }
    @Test
    void 매주_금요일_23시_59분에_해당_주의_이슈_현황을_조회할_때_일주일_구간만_조회한다() throws Exception{
        //given
        //when
        List<AiFilterReport> thisWeekReport = aiFilterReportService.getThisWeekReport();
        //then
        assertThat(thisWeekReport.size()).isEqualTo(2);
        assertThat(thisWeekReport.get(0).getIssueTitle()).isEqualTo("2월 첫째주 topis 이슈 현황");
        assertThat(thisWeekReport.get(0).getIssueContent()).isEqualTo("2월 첫째주 topis 이슈 현황입니다");
        assertThat(thisWeekReport.get(1).getIssueTitle()).isEqualTo("2월 첫째주 topis 이슈 현황2");
        assertThat(thisWeekReport.get(1).getIssueContent()).isEqualTo("2월 첫째주 topis 이슈 현황2입니다");
    }
    @Test
    void AiFilterReport를_저장할_수_있다() throws Exception{
        //given
        AiFilterReportCreate aiFilterReportCreate = AiFilterReportCreate.builder()
                .issueTitle("3월 첫째주 topis 이슈 현황")
                .issueContent("3월 첫째주 topis 이슈 현황입니다")
                .aiFiltered(true)
                .registered(true)
                .failureReason(null)
                .build();
        //when
        AiFilterReport aiFilterReport = aiFilterReportService.save(aiFilterReportCreate);
        //then
        assertThat(aiFilterReport.getIssueTitle()).isEqualTo("3월 첫째주 topis 이슈 현황");
        assertThat(aiFilterReport.getIssueContent()).isEqualTo("3월 첫째주 topis 이슈 현황입니다");
    }

}