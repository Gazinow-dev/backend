package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.notification.controller.port.FcmService;
import com.gazi.gazi_renew.notification.domain.dto.FcmMessage;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadLane;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
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
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;

    @Value("${push.properties.firebase-create-scoped}")
    private String fireBaseCreateScoped;
    @Value("${push.properties.firebase-config-path}")
    private String firebaseConfigPath;
    @Value("${push.properties.api-url}")
    private String API_URL;

    @Override
    @Transactional
    public List<FcmMessage> sendMessageTo(NotificationCreate notificationCreate) throws IOException {
        // FCM 메시지를 리스트로 받음 (각 호선에 대해 개별 메시지 생성)
        List<FcmMessage> messages = makeFcmDto(notificationCreate);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        // 각 메시지를 순회하며 FCM 전송
        for (FcmMessage message : messages) {
            HttpEntity<FcmMessage> entity = new HttpEntity<>(message, headers);

            // FCM 메시지 전송
            ResponseEntity<String> restResponse = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // 하나라도 실패하면 즉시 실패 응답 반환
            if (restResponse.getStatusCode() != HttpStatus.OK) {
                throw ErrorCode.throwFailedFcmMessage();
            }

        }
        return messages;
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

    private List<FcmMessage> makeFcmDto(NotificationCreate notificationCreate) throws JsonProcessingException {
        MyFindRoad myFindRoad = myFindRoadPathRepository.findMyFindRoadPathById(notificationCreate.getMyRoadId());

        Optional<Member> member = memberRepository.findById(myFindRoad.getMember().getId());
        if(member.isEmpty()) {
            throw new EntityNotFoundException("해당 멤버가 존재하지 않습니다.");
        }
        String myPathName = myFindRoad.getRoadName();
        String firebaseToken = member.get().getFirebaseToken();

        Optional<Issue> issue = issueRepository.findById(notificationCreate.getIssueId());
        if(issue.isEmpty()) {
            throw new EntityNotFoundException("해당 이슈가 존재하지 않습니다.");
        }
        ObjectMapper om = new ObjectMapper();

        List<Station> stationList = issue.get().getStationList();

        String pathJson = om.writeValueAsString(MyFindRoadResponse.from(myFindRoad));

        List<String> pathLineNames = myFindRoad.getSubPaths().stream()
                .flatMap(subPath -> subPath.getLanes().stream())
                .map(MyFindRoadLane::getName)
                .distinct()
                .collect(Collectors.toList());

        // 각 Line에 대해 FCM 메시지 생성
        List<FcmMessage> fcmMessages = new ArrayList<>();
        List<Line> lineEntities = issue.get().getLines();
        for (Line line : lineEntities) {
            //내가 저장한 경로의 호선과 같은 이슈만 필터링
            if (!pathLineNames.contains(line.getLineName())) {
                continue;
            }
            // 해당 호선에 속하는 역들만 필터링
            List<Station> stationsForLine = stationList.stream()
                    .filter(station -> station.getLine().equals(line.getLineName()))
                    .collect(Collectors.toList());

            // 필터링한 역들 중 첫 번째 역과 마지막 역 가져오기
            if (!stationsForLine.isEmpty()) {
                Station startStation = stationsForLine.get(0);
                Station endStation = stationsForLine.get(stationsForLine.size() - 1);

                FcmMessage fcmMessage = FcmMessage.createMessage(
                        firebaseToken,
                        makeTitle(myPathName, issue.get().getKeyword()),
                        makeBody(line.getLineName(), startStation.getName(), endStation.getName()),
                        pathJson
                );
                fcmMessages.add(fcmMessage);
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
