package com.gazi.gazi_renew.notification.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.notification.domain.dto.FcmMessage;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.notification.controller.port.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController extends BaseController {
    private final FcmService fcmService;
    private final Response response;

    @PostMapping("/send")
    public ResponseEntity<Response.Body> pushMessage(@RequestBody @Validated NotificationCreate notificationCreate) throws IOException {
        List<FcmMessage> fcmMessages = fcmService.sendMessageTo(notificationCreate);
        // 모든 메시지 전송이 성공하면 성공 응답 반환
        return response.success(fcmMessages, "FCM 메시지 전송 성공", HttpStatus.OK);
    }
}