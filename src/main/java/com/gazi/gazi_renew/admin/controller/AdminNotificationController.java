package com.gazi.gazi_renew.admin.controller;

import com.gazi.gazi_renew.admin.controller.port.AdminNotificationService;
import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;
import com.gazi.gazi_renew.common.controller.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class AdminNotificationController {
    private final AdminNotificationService adminNotificationService;

    @GetMapping("")
    public ResponseEntity<Response.Body> getNotifications() {
        List<AdminNotice> notifications = adminNotificationService.getNotifications();
    }
    @GetMapping("/{noticeId}")
    public ResponseEntity<Response.Body> getNotificationByNoticeId(@PathVariable Long noticeId) {
        adminNotificationService.getNotificationByNoticeId(noticeId);
    }
    @PostMapping("")
    public ResponseEntity<Response.Body> saveNotification(@RequestBody AdminNoticeCreate adminNoticeCreate) {
        adminNotificationService.saveNotification(adminNoticeCreate);
    }
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Response.Body> deleteNotificationByNoticeId(@PathVariable Long noticeId) {
        adminNotificationService.deleteNotificationByNoticeId(noticeId);
    }
    @PatchMapping("")
    public ResponseEntity<Response.Body> deleteNotificationByNoticeId(@RequestBody AdminNoticeCreate adminNoticeCreate) {
        adminNotificationService.updateNotification(adminNoticeCreate);
    }

}
