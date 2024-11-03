package com.gazi.gazi_renew.route.service;

import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.issue.controller.response.IssueResponse;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.domain.MyFindRoadRequest;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse.SubPath;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.service.IssueServiceImpl;
import com.gazi.gazi_renew.route.infrastructure.*;
import com.gazi.gazi_renew.station.infrastructure.LineEntity;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import com.gazi.gazi_renew.station.service.SubwayDataService;
import com.gazi.gazi_renew.station.infrastructure.LineRepository;
import com.gazi.gazi_renew.user.infrastructure.MemberEntity;
import com.gazi.gazi_renew.user.infrastructure.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyFindRoadServiceImpl implements MyFindRoadService {

    private final Response response;
    private final MemberRepository memberRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
    private final MyFindRoadLaneRepository myFindRoadLaneRepository;
    private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;
    private final LineRepository lineRepository;
    private final SubwayDataService subwayDataService;
    private final IssueServiceImpl issueService;
    //회원 인증하고

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getRoutes() {
        try {
            MemberEntity memberEntity = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            List<MyFindRoadPathEntity> myFindRoadPathEntities = myFindRoadPathRepository.findAllByMemberOrderByIdDesc(memberEntity);
            List<MyFindRoadResponse> myFindRoadResponses = getMyFindRoadResponses(myFindRoadPathEntities);
            return response.success(myFindRoadResponses, "마이 길찾기 조회 성공", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getRoutesByMember(Long memberId) {
        try {
            List<MyFindRoadPathEntity> myfindRoadPathListEntity = myFindRoadPathRepository.findByMemberId(memberId);
            List<MyFindRoadResponse> myFindRoadResponses = getMyFindRoadResponses(myfindRoadPathListEntity);

            return response.success(myFindRoadResponses, "마이 길찾기 조회 성공", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Transactional(readOnly = true)
    public MyFindRoadResponse getRouteById(Long id) {
        MyFindRoadPathEntity myFindRoadPathEntity = myFindRoadPathRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id로 마이길찾기 데이터 정보를 찾을 수 없습니다."));

        //서브패스를 찾는다.
        List<MyFindRoadSubPath> myFindRoadSubPaths = myFindRoadSubPathRepository.findAllByMyFindRoadPath(myFindRoadPathEntity);
        ArrayList<SubPath> subPaths = new ArrayList<>();
        // subpathID로 lane과 station을 찾는다.
        for(MyFindRoadSubPath subPath : myFindRoadSubPaths){
            SubPath subPathResponse = SubPath.builder()
                    .way(subPath.getWay())
                    .door(subPath.getDoor())
                    .trafficType(subPath.getTrafficType())
                    .stationCount(subPath.getStationCount())
                    .sectionTime(subPath.getSectionTime())
                    .distance(subPath.getDistance())
                    .build();

            if(subPath.getTrafficType() == 1) {
                MyFindRoadLaneEntity myFindRoadLaneEntity = myFindRoadLaneRepository.findByMyFindRoadSubPath(subPath).orElseThrow(() -> new EntityNotFoundException("lane이 존재하지 않습니다."));
                List<MyFindRoadStationEntity> myFindRoadStationEntities = myFindRoadSubwayRepository.findAllByMyFindRoadSubPath(subPath);

                // response로 가공
                String lineName =  myFindRoadLaneEntity.getName();
                // line entity
                LineEntity lineEntity = lineRepository.findByLineName(lineName).orElseThrow(
                        () -> new EntityNotFoundException("호선으로된 데이터 정보를 찾을 수 없습니다.")
                );
                ArrayList<MyFindRoadResponse.Lane> lanes = new ArrayList<>();
                boolean isDirect = false;
                if(lineName.contains("(급행)")) {
                    isDirect = true;
                }
                MyFindRoadResponse.Lane lane = MyFindRoadResponse.Lane.builder()
                        .name(myFindRoadLaneEntity.getName())
                        .startName(myFindRoadLaneEntity.getStartName())
                        .endName(myFindRoadLaneEntity.getEndName())
                        .stationCode(myFindRoadLaneEntity.getStationCode())
                        .direct(isDirect)
                        .build();
                lanes.add(lane);

                ArrayList<MyFindRoadResponse.Station> stations = new ArrayList<>();
                List<IssueResponse.IssueSummaryDto> issueDtoList = new ArrayList<>();

                for (MyFindRoadStationEntity myFindRoadStationEntity : myFindRoadStationEntities) {

                    StationEntity stationEntity = subwayDataService.getStationByNameAndLine(myFindRoadStationEntity.getStationName(),lineName);
                    List<IssueEntity> issueEntities = stationEntity.getIssueEntities();
                    List<IssueEntity> activeIssueEntities = new ArrayList<>();
                    // activeIssues에 issues 중에서 issue.getExpireDate값이 현재시간보다 앞서는 값만 받도록 설계
                    LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시간

                    for (IssueEntity issueEntity : issueEntities) {
                        if (issueEntity.getExpireDate() != null && issueEntity.getExpireDate().isAfter(currentDateTime)) {
                            activeIssueEntities.add(issueEntity);
                        }
                    }
                    List<IssueResponse.IssueSummaryDto> issueSummaryDtos =IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssueEntities);
                    MyFindRoadResponse.Station station = MyFindRoadResponse.Station.builder()
                            .stationName(myFindRoadStationEntity.getStationName())
                            .index(myFindRoadStationEntity.getIndex())
                            .build();
                    stations.add(station);
                    issueDtoList.addAll(issueSummaryDtos);
                }
                subPathResponse.setLanes(lanes);

                // 호선 이슈리스트 추가 (내 길찾기 역중에서만)
                if(!subPathResponse.getLanes().isEmpty()) {
                    subPathResponse.getLanes().get(0).setIssueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDtoByLine(issueDtoList));
                }
                subPathResponse.setStations(stations);
            }
            subPaths.add(subPathResponse);
        }

        MyFindRoadResponse myFindRoadResponse = MyFindRoadResponse.builder()
                .id(myFindRoadPathEntity.getId())
                .roadName(myFindRoadPathEntity.getName())
                .lastEndStation(myFindRoadPathEntity.getLastEndStation())
                .notification(myFindRoadPathEntity.getNotification())
                .totalTime(myFindRoadPathEntity.getTotalTime())
                .subPaths(subPaths)
                .build();

        return myFindRoadResponse;
    }


        @Override
    public ResponseEntity<Response.Body> addRoute(MyFindRoadRequest request) {
        log.info("길저장 서비스 로직 진입");
        try {
            MemberEntity memberEntity = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            MyFindRoadPathEntity myFindRoadPathEntity = MyFindRoadPathEntity.builder()
                    .memberEntity(memberEntity)
                    .name(request.getRoadName())
                    .totalTime(request.getTotalTime())
                    .firstStartStation(request.getFirstStartStation())
                    .lastEndStation(request.getLastEndStation())
                    .stationTransitCount(request.getStationTransitCount())
                    .notification(false)
                    .build();

            if (myFindRoadPathRepository.existsByNameAndMember(request.getRoadName(), memberEntity)) {
                return response.fail("이미 존재하는 이름입니다.", HttpStatus.CONFLICT);
            }

            myFindRoadPathRepository.save(myFindRoadPathEntity);
            log.info("myFindRoadPath 저장");
            for (MyFindRoadRequest.SubPath subPath : request.getSubPaths()) {
                MyFindRoadSubPath myFindRoadSubPath = MyFindRoadSubPath.builder()
                        .myFindRoadPathEntity(myFindRoadPathEntity)
                        .distance(subPath.getDistance())
                        .sectionTime(subPath.getSectionTime())
                        .stationCount(subPath.getStationCount())
                        .trafficType(subPath.getTrafficType())
                        .door(subPath.getDoor())
                        .way(subPath.getWay())
                        .build();
                myFindRoadSubPathRepository.save(myFindRoadSubPath);
                log.info("myFindRoadSubPath 저장");
                for (MyFindRoadRequest.Lane lain : subPath.getLanes()) {
                    MyFindRoadLaneEntity myFindRoadLaneEntity = MyFindRoadLaneEntity.builder()
                            .myFindRoadSubPath(myFindRoadSubPath)
                            .name(lain.getName())
                            .stationCode(lain.getStationCode())
                            .startName(lain.getStartName())
                            .endName(lain.getEndName())
                            .build();
                    myFindRoadLaneRepository.save(myFindRoadLaneEntity);
                    log.info("MyFindRoadLane 저장 완료");
                }

                for (MyFindRoadRequest.Station station : subPath.getStations()) {
                    MyFindRoadStationEntity myFindRoadStationEntity = MyFindRoadStationEntity.builder()
                            .myFindRoadSubPath(myFindRoadSubPath)
                            .index(station.getIndex())
                            .stationName(station.getStationName())
                            .build();
                    myFindRoadSubwayRepository.save(myFindRoadStationEntity);
                    log.info("MyFindRoadSubway 저장 완료");
                }


            }
            return response.success(myFindRoadPathEntity.getId(), "데이터 저장완료", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Response.Body> deleteRoute(Long id) {
        try {
            MemberEntity memberEntity = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
                    -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            if (myFindRoadPathRepository.existsById(id)) {
                myFindRoadPathRepository.deleteById(id);
                return response.success("삭제 완료");
            } else {
                return response.fail("해당 id로 존재하는 MyFindRoad가 없습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    private List<MyFindRoadResponse> getMyFindRoadResponses(List<MyFindRoadPathEntity> myFindRoadPathEntities) {
        List<MyFindRoadResponse> myFindRoadResponses = new ArrayList<>();
        for (MyFindRoadPathEntity myFindRoadPathEntity : myFindRoadPathEntities) {

            //서브패스를 찾는다.
            List<MyFindRoadSubPath> myFindRoadSubPaths = myFindRoadSubPathRepository.findAllByMyFindRoadPath(myFindRoadPathEntity);
            ArrayList<SubPath> subPaths = new ArrayList<>();
            // subpathID로 lane과 station을 찾는다.
            for(MyFindRoadSubPath subPath : myFindRoadSubPaths){
                SubPath subPathResponse = SubPath.builder()
                        .way(subPath.getWay())
                        .door(subPath.getDoor())
                        .trafficType(subPath.getTrafficType())
                        .stationCount(subPath.getStationCount())
                        .sectionTime(subPath.getSectionTime())
                        .distance(subPath.getDistance())
                        .build();

                if(subPath.getTrafficType() == 1) {
                    MyFindRoadLaneEntity myFindRoadLaneEntity = myFindRoadLaneRepository.findByMyFindRoadSubPath(subPath).orElseThrow(() -> new EntityNotFoundException("lane이 존재하지 않습니다."));
                    List<MyFindRoadStationEntity> myFindRoadStationEntities = myFindRoadSubwayRepository.findAllByMyFindRoadSubPath(subPath);

                    // response로 가공
                    String lineName =  myFindRoadLaneEntity.getName();
                    // line entity
                    LineEntity lineEntity = lineRepository.findByLineName(lineName).orElseThrow(
                            () -> new EntityNotFoundException("호선으로된 데이터 정보를 찾을 수 없습니다.")
                    );
                    ArrayList<MyFindRoadResponse.Lane> lanes = new ArrayList<>();
                    boolean isDirect = false;
                    if(lineName.contains("(급행)")) {
                        isDirect = true;
                    }
                    MyFindRoadResponse.Lane lane = MyFindRoadResponse.Lane.builder()
                            .name(myFindRoadLaneEntity.getName())
                            .startName(myFindRoadLaneEntity.getStartName())
                            .endName(myFindRoadLaneEntity.getEndName())
                            .stationCode(myFindRoadLaneEntity.getStationCode())
                            .direct(isDirect)
                            .build();
                    lanes.add(lane);

                    List<IssueResponse.IssueSummaryDto> issueDtoList = new ArrayList<>();
                    ArrayList<MyFindRoadResponse.Station> stations = new ArrayList<>();

                    for (MyFindRoadStationEntity myFindRoadStationEntity : myFindRoadStationEntities) {
                        StationEntity stationEntity = subwayDataService.getStationByNameAndLine(myFindRoadStationEntity.getStationName(),lineName);
                        List<IssueEntity> issueEntities = stationEntity.getIssueEntities();
                        List<IssueEntity> activeIssueEntities = new ArrayList<>();
                        // activeIssues에 issues 중에서 issue.getExpireDate값이 현재시간보다 앞서는 값만 받도록 설계
                        LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시간

                        for (IssueEntity issueEntity : issueEntities) {
                            if (issueEntity.getExpireDate() != null && issueEntity.getExpireDate().isAfter(currentDateTime)) {
                                activeIssueEntities.add(issueEntity);
                            }
                        }
                        List<IssueResponse.IssueSummaryDto> issueSummaryDtos =IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssueEntities);
                        MyFindRoadResponse.Station station = MyFindRoadResponse.Station.builder()
                                .stationName(myFindRoadStationEntity.getStationName())
                                .index(myFindRoadStationEntity.getIndex())
                                .issueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssueEntities))
                                .build();
                        stations.add(station);

                        issueDtoList.addAll(issueSummaryDtos);
                    }
                    subPathResponse.setLanes(lanes);

                    // 호선 이슈리스트 추가 (내 길찾기 역중에서만)
                    if(!subPathResponse.getLanes().isEmpty()) {
                        subPathResponse.getLanes().get(0).setIssueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDtoByLine(issueDtoList));
                    }
                    subPathResponse.setStations(stations);
                }
                subPaths.add(subPathResponse);
            }

            MyFindRoadResponse myFindRoadResponse = MyFindRoadResponse.builder()
                    .id(myFindRoadPathEntity.getId())
                    .roadName(myFindRoadPathEntity.getName())
                    .lastEndStation(myFindRoadPathEntity.getLastEndStation())
                    .notification(myFindRoadPathEntity.getNotification())
                    .totalTime(myFindRoadPathEntity.getTotalTime())
                    .subPaths(subPaths)
                    .build();
            myFindRoadResponses.add(myFindRoadResponse);
        }
        return myFindRoadResponses;
    }

    @Override
    @Transactional
    public ResponseEntity<Response.Body> updateRouteNotification(Long id, Boolean enabled) {
        try {
            MyFindRoadPathEntity myPath = myFindRoadPathRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            myPath.setNotification(enabled);
            myFindRoadPathRepository.save(myPath);
        } catch (EntityNotFoundException e) {
            return response.fail("해당 id로 존재하는 MyFindRoad가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return response.success("알림 설정(notification "+ enabled + ") 변경 완료");
    }
}
