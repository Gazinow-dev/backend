package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.*;
import com.gazi.gazi_renew.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.dto.FcmMessageDto;
import com.gazi.gazi_renew.dto.FcmSendDto;
import com.gazi.gazi_renew.dto.MyFindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.IssueRepository;
import com.gazi.gazi_renew.repository.MemberRepository;
import com.gazi.gazi_renew.repository.MyFindRoadPathRepository;
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
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
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
        List<String> messages = makeFcmDto(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        // 각 메시지를 순회하며 FCM 전송
        for (String message : messages) {
            HttpEntity<String> entity = new HttpEntity<>(message, headers);

            // FCM 메시지 전송
            ResponseEntity<String> restResponse = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // 하나라도 실패하면 즉시 실패 응답 반환
            if (restResponse.getStatusCode() != HttpStatus.OK) {
                return response.fail("FCM 메시지 전송 실패", HttpStatus.BAD_REQUEST);
            }
        }

        // 모든 메시지 전송이 성공하면 성공 응답 반환
        return response.success("FCM 메시지 전송 성공");
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

    private List<String> makeFcmDto(FcmSendDto fcmSendDto) throws JsonProcessingException {
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
        MyFindRoadResponse routeById = myFindRoadService.getRouteById(fcmSendDto.getMyRoadId());
        List<Station> stations = issue.get().getStations();

        String pathJson = om.writeValueAsString(routeById);

        // 각 Line에 대해 FCM 메시지 생성
        List<String> fcmMessages = new ArrayList<>();
        List<Line> lines = issue.get().getLines();
        for (Line line : lines) {
            // 해당 호선에 속하는 역들만 필터링
            List<Station> stationsForLine = stations.stream()
                    .filter(station -> station.getLine().equals(line.getLineName()))
                    .collect(Collectors.toList());

            // 필터링한 역들 중 첫 번째 역과 마지막 역 가져오기
            if (!stationsForLine.isEmpty()) {
                Station startStation = stationsForLine.get(0);
                Station endStation = stationsForLine.get(stationsForLine.size() - 1);

                FcmMessageDto fcmMessageDto = FcmMessageDto.createMessage(
                        firebaseToken,
                        makeTitle(myPathName, issue.get().getKeyword()),
                        makeBody(line.getLineName(), startStation.getName(), endStation.getName()),
                        pathJson
                );
                fcmMessages.add(om.writeValueAsString(fcmMessageDto));
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
