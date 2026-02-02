package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.controller.port.AdminNotificationService;
import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeUpdate;
import com.gazi.gazi_renew.admin.service.port.AdminNoticeRepository;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import com.gazi.gazi_renew.notification.service.port.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNotificationService {
    private final MemberRepository memberRepository;
    private final AdminNoticeRepository adminNoticeRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final ClockHolder clockHolder;
    @Override
    @Transactional(readOnly = true)
    public Page<AdminNotice> getNotifications(Pageable pageable) {
        return adminNoticeRepository.getNotifications(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNotice getNotificationByNoticeId(Long noticeId) {
        return adminNoticeRepository.getNotificationByNoticeId(noticeId);
    }

    @Override
    @Transactional
    public AdminNotice saveNotification(AdminNoticeCreate adminNoticeCreate) {
        AdminNotice adminNotice = adminNoticeRepository.saveNotification(AdminNotice.from(adminNoticeCreate, clockHolder));
        List<Long> idList = memberRepository.findAllIdList();

        List<NotificationHistory> histories = idList.stream()
                .map(memberId -> NotificationHistory.saveHistory(
                        memberId,
                        adminNotice.getNoticeId(),
                        adminNotice.getNoticeTitle(),
                        adminNotice.getNoticeContent(),
                        IssueKeyword.관리자,
                        clockHolder.now()
                ))
                .toList();
        notificationHistoryRepository.saveAll(histories);
        return adminNotice;
    }

    @Override
    @Transactional
    public void deleteNotificationByNoticeId(Long noticeId) {
        adminNoticeRepository.deleteNotificationByNoticeId(noticeId);
    }

    @Override
    @Transactional
    public void updateNotification(AdminNoticeUpdate adminNoticeUpdate) {
        AdminNotice adminNotice = adminNoticeRepository.getNotificationByNoticeId(adminNoticeUpdate.getNoticeId());
        adminNotice = adminNotice.update(adminNoticeUpdate, clockHolder);
        adminNoticeRepository.updateNotification(adminNotice);
    }
}
