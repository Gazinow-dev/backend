package com.gazi.gazi_renew.admin.service.port;


import com.gazi.gazi_renew.admin.domain.AdminNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminNoticeRepository {

    Page<AdminNotice> getNotifications(Pageable pageable);

    AdminNotice getNotificationByNoticeId(Long noticeId);

    AdminNotice saveNotification(AdminNotice adminNotice);

    void deleteNotificationByNoticeId(Long noticeId);

    void updateNotification(AdminNotice update);
}
