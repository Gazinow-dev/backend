package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.Station;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        String message = makeFcmDto(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        ResponseEntity<String> restResponse = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return restResponse.getStatusCode() == HttpStatus.OK ? response.success("성공") : response.fail("실패", HttpStatus.BAD_REQUEST);
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
        MyFindRoadResponse routeById = myFindRoadService.getRouteById(fcmSendDto.getMyRoadId());
        List<Station> stations = issue.get().getStations();

        String pathJson = om.writeValueAsString(routeById);

        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(firebaseToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(makeTitle(myPathName, issue.get().getKeyword()))
                                .body(makeBody(issue.get().getLine(), stations.get(0).getName(), stations.get(stations.size() - 1).getName()))
                                .build()
                        )
                        .data(FcmMessageDto.Data.builder().path(pathJson)
                                .build()).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }

    private String makeBody(String line, String startStationName, String endStationName) {
        return line + startStationName + " - " + endStationName + " 방면";
    }

    private String makeTitle(String pathName, IssueKeyword issueType) {
        return pathName + " 경로에 [" + issueType + "] 이슈가 생겼어요";
    }
}
