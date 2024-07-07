package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.FcmSendDto;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface FcmService {
    ResponseEntity<Response.Body> sendMessageTo(FcmSendDto fcmSendDto) throws IOException;
}
