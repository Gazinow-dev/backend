package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.dto.FcmMessageDto;
import com.gazi.gazi_renew.dto.FcmSendDto;
import com.gazi.gazi_renew.dto.Response;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class FcmServiceImpl implements FcmService {
    private final Response response;

    @Value("${push.properties.firebase-create-scoped}")
    String fireBaseCreateScoped;

    @Override
    public ResponseEntity<Response.Body> sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        String message = makeMessage(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        String API_URL = "https://fcm.googleapis.com/v1/projects/gazi-81f38/messages:send";
        ResponseEntity<String> restResponse = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return restResponse.getStatusCode() == HttpStatus.OK ? response.success("성공") : response.fail("실패", HttpStatus.BAD_REQUEST);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/gazi-81f38-firebase-adminsdk-g89dw-f57852c34b.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped((Collections.singletonList(fireBaseCreateScoped)));

        // Ensure credentials are refreshed
        googleCredentials.refreshIfExpired();

        // Get the refreshed access token
        AccessToken token = googleCredentials.getAccessToken();
        if (token == null) {
            // Refresh manually if it's still null
            googleCredentials.refresh();
            token = googleCredentials.getAccessToken();
        }

        if (token == null) {
            throw new IOException("Failed to obtain access token.");
        }
        return token.getTokenValue();
    }

    private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("body", fcmSendDto.getBody());
        jsonObject.put("issueType", fcmSendDto.getIssueType());
        jsonObject.put("myRoadId", fcmSendDto.getMyRoadId());

        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(fcmSendDto.getToken())
                        .notification(FcmMessageDto.Notification.builder()
                                .title(fcmSendDto.getTitle())
                                .body(jsonObject.toString())
                                .build()
                        ).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }
}
