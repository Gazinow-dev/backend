package com.gazi.gazi_renew.admin.service.port;


import com.gazi.gazi_renew.admin.domain.AdminNotice;

import java.util.List;

public interface AdminNoticeRepository {

    List<AdminNotice> getNotifications();

    AdminNotice getNotificationByNoticeId(Long noticeId);

    AdminNotice saveNotification(AdminNotice adminNotice);

    void deleteNotificationByNoticeId(Long noticeId);

    void updateNotification(AdminNotice update);
}
