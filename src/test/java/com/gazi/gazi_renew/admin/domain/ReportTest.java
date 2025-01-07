package com.gazi.gazi_renew.admin.domain;

import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;
import com.gazi.gazi_renew.mock.TestClockHolder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportTest {
    @Test
    void 댓글_신고는_신고_사유_선택지가_필수로_입력돼야_한다() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_LANGUAGE")
                .reasonDescription("모욕적인 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));

        //then
        assertThat(report.getReportReason().getDescription()).isEqualTo("음란성 댓글, 비속어, 폭언, 비하 등 불쾌한 내용을 포함하고 있어요");
    }

    @Test
    void 댓글은_관리자가_승인하기_전까지_대기_상태를_유지해야_한다() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_LANGUAGE")
                .reasonDescription("모욕적인 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.PENDING);
    }

    @Test
    void 댓글은_관리자가_제제_적합_판단을_내릴_경우_승인_상태로_변경돼야_한다() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_LANGUAGE")
                .reasonDescription("모욕적인 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();
        Penalty penalty = Penalty.builder()
                .penaltyId(1L)
                .memberId(reportedId)
                .startDate(now)
                .expireDate(now)
                .build();
        int reportCount = 2;

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.approveReport("OTHER_VIOLATIONS", penalty, reportCount, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(penalty.getExpireDate()).isAfter(now); // Penalty가 연장되었는지 확인
    }

    @Test
    void 댓글은_관리자가_제제_부적합_판단을_내릴_경우_거절_상태로_변경돼야_한다() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_LANGUAGE")
                .reasonDescription("모욕적인 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.rejectReport();

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.REJECTED);
    }

    @Test
    void 제재기준이_ADVERTISEMENT_일_경우_댓글_작성_1년_제한() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_CONTENT")
                .reasonDescription("광고성 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();
        Penalty penalty = Penalty.builder()
                .penaltyId(1L)
                .memberId(reportedId)
                .startDate(now)
                .expireDate(now)
                .build();

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.approveReport("ADVERTISEMENT", penalty, 1, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(penalty.getExpireDate()).isEqualTo(now.plusDays(365)); // 1년 연장 확인
    }
    @Test
    void 제재기준이_OTHER_VIOLATIONS_이고_신고_횟수가_1회일_경우_댓글_작성_1일_제한() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_LANGUAGE")
                .reasonDescription("광고성 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();
        Penalty penalty = Penalty.builder()
                .penaltyId(1L)
                .memberId(reportedId)
                .startDate(now)
                .expireDate(now)
                .build();

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.approveReport("OTHER_VIOLATIONS", penalty, 1, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(penalty.getExpireDate()).isEqualTo(now.plusDays(1));
    }
    @Test
    void 제재기준이_OTHER_VIOLATIONS_이고_신고_횟수가_5회_이상일_경우_댓글_작성_1일_제한() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("INAPPROPRIATE_LANGUAGE")
                .reasonDescription("광고성 댓글입니다")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();
        Penalty penalty = Penalty.builder()
                .penaltyId(1L)
                .memberId(reportedId)
                .startDate(now)
                .expireDate(now)
                .build();

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.approveReport("OTHER_VIOLATIONS", penalty, 5, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(penalty.getExpireDate()).isEqualTo(now.plusDays(30));
    }
    @Test
    void 제재기준이_FALSE_REPORT_이고_신고_횟수가_1회일_경우_댓글_작성_7일_제한() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("MISLEADING_INFORMATION")
                .reasonDescription("허위 신고로 판단됨")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();
        Penalty penalty = Penalty.builder()
                .penaltyId(1L)
                .memberId(reportedId)
                .startDate(now)
                .expireDate(now)
                .build();
        int reportCount = 1;

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.approveReport("FALSE_REPORT", penalty, reportCount, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(penalty.getExpireDate()).isEqualTo(now.plusDays(7)); // 7일 연장 확인
    }
    @Test
    void 제재기준이_FALSE_REPORT_이고_신고_횟수가_2회일_경우_댓글_작성_14일_제한() throws Exception {
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(1L)
                .reason("MISLEADING_INFORMATION")
                .reasonDescription("허위 신고로 판단됨")
                .build();
        Long reporterId = 1L;
        Long reportedId = 2L;
        LocalDateTime now = LocalDateTime.now();
        Penalty penalty = Penalty.builder()
                .penaltyId(1L)
                .memberId(reportedId)
                .startDate(now)
                .expireDate(now)
                .build();
        int reportCount = 2;

        //when
        Report report = Report.create(reportCreate, reporterId, reportedId, new TestClockHolder(now));
        report = report.approveReport("FALSE_REPORT", penalty, reportCount, new TestClockHolder(now));

        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(penalty.getExpireDate()).isEqualTo(now.plusDays(14)); // 7일 연장 확인
    }
}
