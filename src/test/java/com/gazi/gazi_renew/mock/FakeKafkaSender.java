package com.gazi.gazi_renew.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.KafkaSender;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class FakeKafkaSender implements KafkaSender {
    private final List<Object> sentNotifications = new ArrayList<>();

    @Override
    public void sendNotification(Issue issue, List<Line> lineList, List<Station> stationList) throws JsonProcessingException {
        // 저장된 NotificationCreate 기록 리스트에 추가
        NotificationCreate notificationCreate = NotificationCreate.builder()
                .issueId(issue.getId())
                .myRoadId(1L)
                .build();
        sentNotifications.add(notificationCreate);
    }

}
