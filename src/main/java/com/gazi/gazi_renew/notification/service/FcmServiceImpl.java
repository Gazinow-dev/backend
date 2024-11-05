package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.notification.controller.port.FcmService;
import com.gazi.gazi_renew.notification.domain.FcmMessageDto;
import com.gazi.gazi_renew.notification.domain.FcmSendDto;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.station.infrastructure.LineEntity;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import com.gazi.gazi_renew.member.infrastructure.jpa.MemberJpaRepository;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathRepository;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private final Response response;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final IssueJpaRepository issueJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final MyFindRoadService myFindRoadService;

    @Value("${push.properties.firebase-create-scoped}")
    private String fireBaseCreateScoped;
    @Value("${push.properties.firebase-config-path}")
    private String firebaseConfigPath;
    @Value("${push.properties.api-url}")
    private String API_URL;

    @Override
    @Transactional
    public ResponseEntity<Response.Body> sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        // FCM 메시지를 리스트로 받음 (각 호선에 대해 개별 메시지 생성)
        List<FcmMessageDto> messages = makeFcmDto(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        // 각 메시지를 순회하며 FCM 전송
        for (FcmMessageDto message : messages) {
            HttpEntity<FcmMessageDto> entity = new HttpEntity<>(message, headers);

            // FCM 메시지 전송
            ResponseEntity<String> restResponse = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // 하나라도 실패하면 즉시 실패 응답 반환
            if (restResponse.getStatusCode() != HttpStatus.OK) {
                return response.fail("FCM 메시지 전송 실패", HttpStatus.BAD_REQUEST);
            }
        }

        // 모든 메시지 전송이 성공하면 성공 응답 반환
        return response.success(messages, "FCM 메시지 전송 성공", HttpStatus.OK);
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped((Collections.singletonList(fireBaseCreateScoped)));

        googleCredentials.refreshIfExpired();

        // access token 가져오기
        AccessToken token = googleCredentials.getAccessToken();
        if (token == null) {
            googleCredentials.refresh();
            token = googleCredentials.getAccessToken();
        }

        if (token == null) {
            throw new IOException("Failed to obtain access token.");
        }
        return token.getTokenValue();
    }

    private List<FcmMessageDto> makeFcmDto(FcmSendDto fcmSendDto) throws JsonProcessingException {
        Optional<MyFindRoadPathEntity> myPath = Optional.ofNullable(myFindRoadPathRepository.findMyFindRoadPathById(fcmSendDto.getMyRoadId()));
        if(myPath.isEmpty()) {
            throw new EntityNotFoundException("해당 경로가 존재하지 않습니다.");
        }

        Optional<MemberEntity> member = memberJpaRepository.findById(myPath.get().getMemberEntity().getId());
        if(member.isEmpty()) {
            throw new EntityNotFoundException("해당 멤버가 존재하지 않습니다.");
        }
        String myPathName = myPath.get().getName();
        String firebaseToken = member.get().getFirebaseToken();


        Optional<IssueEntity> issue = issueJpaRepository.findById(fcmSendDto.getIssueId());
        if(issue.isEmpty()) {
            throw new EntityNotFoundException("해당 이슈가 존재하지 않습니다.");
        }
        ObjectMapper om = new ObjectMapper();
        MyFindRoadResponse routeById = myFindRoadService.getRouteById(fcmSendDto.getMyRoadId());
        List<StationEntity> stationEntities = issue.get().getStationEntities();

        String pathJson = om.writeValueAsString(routeById);

        // 각 Line에 대해 FCM 메시지 생성
        List<FcmMessageDto> fcmMessages = new ArrayList<>();
        List<LineEntity> lineEntities = issue.get().getLineEntities();
        for (LineEntity lineEntity : lineEntities) {
            // 해당 호선에 속하는 역들만 필터링
            List<StationEntity> stationsForLine = stationEntities.stream()
                    .filter(station -> station.getLine().equals(lineEntity.getLineName()))
                    .collect(Collectors.toList());

            // 필터링한 역들 중 첫 번째 역과 마지막 역 가져오기
            if (!stationsForLine.isEmpty()) {
                StationEntity startStationEntity = stationsForLine.get(0);
                StationEntity endStationEntity = stationsForLine.get(stationsForLine.size() - 1);

                FcmMessageDto fcmMessageDto = FcmMessageDto.createMessage(
                        firebaseToken,
                        makeTitle(myPathName, issue.get().getKeyword()),
                        makeBody(lineEntity.getLineName(), startStationEntity.getName(), endStationEntity.getName()),
                        pathJson
                );
                fcmMessages.add(fcmMessageDto);
            }
        }

        return fcmMessages;  // 각 Line에 대해 생성된 FCM 메시지 반환
    }

    private String makeBody(String line, String startStationName, String endStationName) {
        String newLine = line.replace("수도권", "").trim();
        return newLine + " " + startStationName + " - " + endStationName + " 방면";
    }

    private String makeTitle(String pathName, IssueKeyword issueType) {
        return pathName + " 경로에 [" + issueType + "] 이슈가 생겼어요";
    }
}
