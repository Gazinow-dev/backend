package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.issue.service.port.IssueLineRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.notification.controller.port.FcmService;
import com.gazi.gazi_renew.notification.domain.FcmMessageTemplate;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import com.gazi.gazi_renew.notification.domain.dto.FcmMessage;
import com.gazi.gazi_renew.notification.domain.dto.NextDayNotificationFcmMessage;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import com.gazi.gazi_renew.notification.service.port.NotificationHistoryRepository;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.LineRepository;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final IssueStationRepository issueStationRepository;
    private final IssueLineRepository issueLineRepository;
    private final LineRepository lineRepository;
    private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;
    private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
    private final SubwayRepository subwayRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    @Value("${push.properties.firebase-create-scoped}")
    private String fireBaseCreateScoped;
    @Value("${push.properties.firebase-config-path}")
    private String firebaseConfigPath;
    @Value("${push.properties.api-url}")
    private String API_URL;

    @Override
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
    public List<NextDayNotificationFcmMessage> nextDayIssueSendMessageTo() throws IOException {
        List<NextDayNotificationFcmMessage> targets = makeNextDayFcmDto();
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        // 각 메시지를 순회하며 FCM 전송
        for (NextDayNotificationFcmMessage message : targets) {
            HttpEntity<NextDayNotificationFcmMessage> entity = new HttpEntity<>(message, headers);

            // FCM 메시지 전송
            ResponseEntity<String> restResponse = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // 하나라도 실패하면 즉시 실패 응답 반환
            if (restResponse.getStatusCode() != HttpStatus.OK) {
                throw ErrorCode.throwFailedFcmMessage();
            }
        }
        return targets;
    }
    private List<NextDayNotificationFcmMessage> makeNextDayFcmDto() throws JsonProcessingException {
        LocalDateTime endOfTomorrow = LocalDateTime.of(
                LocalDate.now().plusDays(1),
                LocalTime.of(23, 59, 59)
        );

        //익일 이슈 알림을 허용한 유저 가져오가
        List<Member> memberList = memberRepository.findByNextDayNotificationEnabled(Boolean.TRUE);
        if (memberList.isEmpty()) {
            throw new EntityNotFoundException("해당 멤버가 존재하지 않습니다.");
        }
        List<NextDayNotificationFcmMessage> nextDayFcmMessageList = new ArrayList<>();
        List<Long> myFindRoadIdList = new ArrayList<>();
        // 가져온 유저의 경로에서 이슈가 있는지 가져오기
        for (Member member : memberList) {
            int issueCnt = 0;
            Long memberId = member.getId();
            List<MyFindRoad> myFindRoadList = myFindRoadPathRepository.findAllByMemberOrderByIdDesc(member);
            for (MyFindRoad myFindRoad : myFindRoadList) {
                myFindRoad = getStation(myFindRoad);
                for (MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getSubPaths()) {
                    if (myFindRoadSubPath.getTrafficType() != 1) continue; // 지하철만

                    for (MyFindRoadStation station : myFindRoadSubPath.getStations()) {
                        List<Issue> issueList = station.getIssueList();
                        if (issueList == null) continue;

                        for (Issue issue : issueList) {
                            if (issue.getStartDate() != null && issue.getStartDate().isAfter(LocalDateTime.now()) && issue.getStartDate().isBefore(endOfTomorrow)) {
                                issueCnt += 1;
                            }
                        }
                    }

                }
                if (issueCnt > 0) {
                    myFindRoadIdList.add(myFindRoad.getId());
                }
            }
            if (!myFindRoadIdList.isEmpty()) {
                NextDayNotificationFcmMessage message = NextDayNotificationFcmMessage.createMessage(
                        myFindRoadIdList,
                        member.getFirebaseToken(),
                        FcmMessageTemplate.NEXT_DAY_ISSUE.getTitle());
                nextDayFcmMessageList.add(message);
            }
        }
        return nextDayFcmMessageList;
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
        myFindRoad = getStation(myFindRoad);
        Optional<Member> member = memberRepository.findById(myFindRoad.getMemberId());
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
        List<IssueStation> issueStationList = issueStationRepository.findAllByIssue(issue.get().getId());

        List<Station> stationList = issueStationList.stream()
                .map(IssueStation::getStation).collect(Collectors.toList());

        String pathJson = om.writeValueAsString(MyFindRoadResponse.from(myFindRoad));

        List<String> pathLineNames = myFindRoad.getSubPaths().stream()
                .map(MyFindRoadSubPath::getName)
                .distinct()
                .collect(Collectors.toList());

        // 각 Line에 대해 FCM 메시지 생성
        List<FcmMessage> fcmMessages = new ArrayList<>();
        List<IssueLine> issueLineList = issueLineRepository.findAllByIssue(issue.get().getId());

        List<Line> lineList = issueLineList.stream().map(issueLine -> lineRepository.findById(issueLine.getLine().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        for (Line line : lineList) {
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
                String title = makeTitle(myPathName, issue.get().getKeyword());
                String body = makeBody(line.getLineName(), startStation.getName(), endStation.getName());

                NotificationHistory notificationHistory = NotificationHistory.saveHistory(member.get().getId(), issue.get().getId(), body
                        , title, issue.get().getKeyword(), issue.get().getStartDate());
                NotificationHistory savedHistory = notificationHistoryRepository.save(notificationHistory);
                if (notificationCreate.getSendNotification()) {
                    FcmMessage fcmMessage = FcmMessage.createMessage(
                            savedHistory.getId(),
                            firebaseToken,
                            title,
                            body,
                            pathJson
                    );
                    fcmMessages.add(fcmMessage);
                }
                return fcmMessages;
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
    private MyFindRoad getStation(MyFindRoad myFindRoad) {
        // 업데이트된 SubPath 리스트를 저장할 컬렉션
        List<MyFindRoadSubPath> updatedSubPaths = new ArrayList<>();

        // SubPath를 조회
        List<MyFindRoadSubPath> myFindRoadSubPathList = myFindRoadSubPathRepository.findByMyFindRoadPathId(myFindRoad.getId());

        for (MyFindRoadSubPath myFindRoadSubPath : myFindRoadSubPathList) {
            List<MyFindRoadStation> updatedStations = new ArrayList<>();

            // SubPath에 포함된 Station 조회
            List<MyFindRoadStation> myFindRoadStationList = myFindRoadSubwayRepository.findAllByMyFindRoadSubPathId(myFindRoadSubPath.getId());
            for (MyFindRoadStation myFindRoadStation : myFindRoadStationList) {
                String line = myFindRoadSubPath.getName();
                if (line.equals("수도권 9호선(급행)")) {
                    line = "수도권 9호선";
                }

                // Station 조회
                List<Station> stationList = subwayRepository.findByNameContainingAndLine(myFindRoadStation.getStationName(), line);
                Station station = Station.toFirstStation(myFindRoadStation.getStationName(), stationList);

                if (station != null) {
                    // Station에 연결된 Issue 조회
                    List<IssueStation> issueStationList = issueStationRepository.findAllByStationId(station.getId());
                    List<Issue> issueList = issueStationList.stream().map(IssueStation::getIssue).collect(Collectors.toList());
                    // Issue 리스트를 업데이트한 새로운 MyFindRoadStation 객체 생성
                    myFindRoadStation = myFindRoadStation.updateIssueList(issueList);
                }
                updatedStations.add(myFindRoadStation); // 변경된 Station 리스트에 추가
            }

            // 업데이트된 Stations를 반영한 SubPath 객체 생성
            MyFindRoadSubPath updatedSubPath = myFindRoadSubPath.updateStations(updatedStations);
            updatedSubPaths.add(updatedSubPath); // 변경된 SubPath 리스트에 추가
        }

        // 업데이트된 SubPaths를 반영한 MyFindRoad 객체 생성 및 반환
        return myFindRoad.updateSubPaths(updatedSubPaths);
    }

}
