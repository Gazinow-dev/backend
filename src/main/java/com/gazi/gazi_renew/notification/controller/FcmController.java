package com.gazi.gazi_renew.notification.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.notification.domain.NotificationCreate;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.notification.controller.port.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController extends BaseController {
    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<Response.Body> pushMessage(@RequestBody @Validated NotificationCreate notificationCreate) throws IOException {
        return fcmService.sendMessageTo(notificationCreate);
    }
}