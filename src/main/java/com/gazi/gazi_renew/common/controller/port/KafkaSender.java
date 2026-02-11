package com.gazi.gazi_renew.common.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;

import java.util.List;

public interface KafkaSender {
    void sendNotification(Long issueId, List<IssueLine> lineList, List<IssueStation> stationList) throws JsonProcessingException;
}
