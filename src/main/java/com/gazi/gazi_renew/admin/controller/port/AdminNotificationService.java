package com.gazi.gazi_renew.admin.controller.port;

import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;

import java.util.List;

public interface AdminNotificationService {

    List<AdminNotice> getNotifications();

    AdminNotice getNotificationByNoticeId(Long noticeId);

    AdminNotice saveNotification(AdminNoticeCreate adminNoticeCreate);


    void deleteNotificationByNoticeId(Long noticeId);

    AdminNotice updateNotification(AdminNoticeCreate adminNoticeCreate);
}
