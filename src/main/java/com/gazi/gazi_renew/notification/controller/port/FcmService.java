package com.gazi.gazi_renew.notification.controller.port;

import com.gazi.gazi_renew.notification.domain.dto.FcmMessage;
import com.gazi.gazi_renew.notification.domain.dto.NextDayNotificationFcmMessage;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface FcmService {
    List<FcmMessage> sendMessageTo(NotificationCreate notificationCreate) throws IOException;
    void nextDayIssueSendMessageTo() throws IOException;
}
