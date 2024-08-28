package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.dto.FcmMessageDto;
import com.gazi.gazi_renew.dto.FcmSendDto;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.IssueRepository;
import com.gazi.gazi_renew.repository.MemberRepository;
import com.gazi.gazi_renew.repository.MyFindRoadPathRepository;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FcmServiceImpl implements FcmService {
    private final Response response;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final MyFindRoadService myFindRoadService;

    @Value("${push.properties.firebase-create-scoped}")
    String fireBaseCreateScoped;

    @Override
    public ResponseEntity<Response.Body> sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        String message = makeFcmDto(fcmSendDto);
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

//    private String makeMessage(FcmDto fcmDto) throws JsonProcessingException {
//        ObjectMapper om = new ObjectMapper();
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("body", fcmDto.getBody());
//        jsonObject.put("issueType", fcmDto.getIssueType());
//        jsonObject.put("myRoadId", fcmDto.getMyRoadId());
//
//        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
//                .message(FcmMessageDto.Message.builder()
//                        .token(fcmDto.getToken())
//                        .notification(FcmMessageDto.Notification.builder()
//                                .title(fcmDto.getTitle())
//                                .body(jsonObject.toString())
//                                .build()
//                        ).build()).validateOnly(false).build();
//
//        return om.writeValueAsString(fcmMessageDto);
//    }

/*    private JSONObject getDetails(Long userMyRoadId) {
        Optional<MyFindRoadPath> myPath = Optional.ofNullable(myFindRoadPathRepository.findMyFindRoadPathById(userMyRoadId));
        if(myPath.isEmpty()) {
            throw new EntityNotFoundException("해당 경로가 존재하지 않습니다.");
        } else {
            JSONObject obj = new JSONObject();
            Optional<Member> member = memberRepository.findById(myPath.get().getMember().getId());
            if(member.isEmpty()) {
                throw new EntityNotFoundException("해당 멤버가 존재하지 않습니다.");
            }
            obj.put("myPathName", myPath.get().getName());
            obj.put("firebaseToken", member.get().getFirebaseToken());
            return obj;
        }
    }*/

    private String makeFcmDto(FcmSendDto fcmSendDto) throws JsonProcessingException {
        Optional<MyFindRoadPath> myPath = Optional.ofNullable(myFindRoadPathRepository.findMyFindRoadPathById(fcmSendDto.getMyRoadId()));
        if(myPath.isEmpty()) {
            throw new EntityNotFoundException("해당 경로가 존재하지 않습니다.");
        }

        Optional<Member> member = memberRepository.findById(myPath.get().getMember().getId());
        if(member.isEmpty()) {
            throw new EntityNotFoundException("해당 멤버가 존재하지 않습니다.");
        }
        String myPathName = myPath.get().getName();
        String firebaseToken = member.get().getFirebaseToken();


        Optional<Issue> issue = issueRepository.findById(fcmSendDto.getIssueId());
        if(issue.isEmpty()) {
            throw new EntityNotFoundException("해당 이슈가 존재하지 않습니다.");
        }
        ObjectMapper om = new ObjectMapper();

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("myRoad", myPathName);
//        jsonObject.put("body", fcmDto.getBody());
//        jsonObject.put("issueType", issue.get().getKeyword());

        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
               .message(FcmMessageDto.Message.builder()
                       .token(firebaseToken)
                       .notification(FcmMessageDto.Notification.builder()
                               .title(makeTitle(myPathName, issue.get().getKeyword()))
                               .body("body test")
                               .build()
                        )
                       .data(FcmMessageDto.Data.builder().path(
                               myFindRoadService.getRouteById(fcmSendDto.getMyRoadId())
                       ).build()).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }

    private String makeTitle(String pathName, IssueKeyword issueType) {
        return pathName + " 경로에 [" + issueType + "] 이슈가 생겼어요";
    }
}
