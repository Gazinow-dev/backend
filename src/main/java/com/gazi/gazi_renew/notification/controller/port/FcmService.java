package com.gazi.gazi_renew.notification.controller.port;

import com.gazi.gazi_renew.notification.domain.FcmSendDto;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public interface FcmService {
    ResponseEntity<Response.Body> sendMessageTo(FcmSendDto fcmSendDto) throws IOException;
}
