package com.gazi.gazi_renew.notification.scheduler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.service.kafka.NotificationSender;
import com.gazi.gazi_renew.issue.service.port.IssueLineRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import com.gazi.gazi_renew.notification.controller.port.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final FcmService fcmService;
    private final RedisUtilService redisUtilService;
    private final IssueLineRepository issueLineRepository;
    private final IssueStationRepository issueStationRepository;
    private final NotificationSender notificationSender;
    private static final String SCHEDULE_KEY = "issue:schedule";
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void nextDayIssueNotify() throws IOException {
        fcmService.nextDayIssueSendMessageTo();
    }
    @Scheduled(fixedDelay = 5000)
    public void processScheduledIssues() throws JsonProcessingException {

        long now = System.currentTimeMillis() / 1000;

        Set<String> issueList = redisUtilService.getDueIssueIds(SCHEDULE_KEY, now);
        if (!issueList.isEmpty()) {
            log.info("스케줄링 처리 대상 이슈 발견 - 개수: {}건, IDs: {}", issueList.size(), issueList);
        }
        for (String issueIdStr : issueList) {
            Long issueId = Long.parseLong(issueIdStr);
            List<IssueLine> issueLineList = issueLineRepository.findAllByIssue(issueId);
            List<IssueStation> issueStationList = issueStationRepository.findAllByIssue(issueId);
            notificationSender.sendNotification(issueId, issueLineList, issueStationList);
        }
    }
}
