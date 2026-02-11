package com.gazi.gazi_renew.mock.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.KafkaSender;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class FakeKafkaSender implements KafkaSender {
    private final List<Object> sentNotifications = new ArrayList<>();

    @Override
    public void sendNotification(Long issueId, List<IssueLine> lineList, List<IssueStation> stationList) throws JsonProcessingException {
        // 저장된 NotificationCreate 기록 리스트에 추가
        NotificationCreate notificationCreate = NotificationCreate.builder()
                .issueId(issueId)
                .myRoadId(1L)
                .build();
        sentNotifications.add(notificationCreate);
    }

}
