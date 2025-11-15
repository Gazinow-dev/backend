package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.controller.port.AdminNotificationService;
import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNotificationService {
    private final AdminNoticeRepository adminNoticeRepository;
    private final ClockHolder clockHolder;
    @Override
    @Transactional(readOnly = true)
    public List<AdminNotice> getNotifications() {
        return adminNoticeRepository.getNotifications();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNotice getNotificationByNoticeId(Long noticeId) {
        return adminNoticeRepository.getNotificationByNoticeId(noticeId);
    }

    @Override
    @Transactional
    public AdminNotice saveNotification(AdminNoticeCreate adminNoticeCreate) {
        return adminNoticeRepository.saveNotification(AdminNotice.from(adminNoticeCreate, clockHolder));
    }

    @Override
    @Transactional
    public void deleteNotificationByNoticeId(Long noticeId) {
        adminNoticeRepository.deleteNotificationByNoticeId(noticeId)
    }

    @Override
    @Transactional
    public AdminNotice updateNotification(AdminNoticeCreate adminNoticeCreate) {
        return adminNoticeRepository.updateNotification(AdminNotice.update(adminNoticeCreate, clockHolder));
    }
}
