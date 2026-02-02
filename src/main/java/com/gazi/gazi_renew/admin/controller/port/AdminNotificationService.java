package com.gazi.gazi_renew.admin.controller.port;

import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminNotificationService {

    Page<AdminNotice> getNotifications(Pageable pageable);

    AdminNotice getNotificationByNoticeId(Long noticeId);

    AdminNotice saveNotification(AdminNoticeCreate adminNoticeCreate);


    void deleteNotificationByNoticeId(Long noticeId);

    void updateNotification(AdminNoticeUpdate adminNoticeUpdate);
}
