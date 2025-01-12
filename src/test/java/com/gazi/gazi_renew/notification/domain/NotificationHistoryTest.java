package com.gazi.gazi_renew.notification.domain;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.mock.common.TestClockHolder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationHistoryTest {

    @Test
    void NotificationHistory를_생성할_수_있다() throws Exception{
        //given
        Long memberId = 1L;
        Long issueId = 1L;
        String title = "알림 히스토리 테스트";
        String body = "알림 히스토리 body 테스트";
        IssueKeyword issueKeyword = IssueKeyword.시위;

        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);
        //when
        NotificationHistory notificationHistory = NotificationHistory.saveHistory(memberId, issueId, title, body, issueKeyword, testClockHolder);
        //then
        assertThat(notificationHistory.getNotificationTitle()).isEqualTo(title);
        assertThat(notificationHistory.getNotificationBody()).isEqualTo(body);
        assertThat(notificationHistory.isRead()).isEqualTo(false);
        assertThat(notificationHistory.getStartDate()).isEqualTo(newTime);
    }

}