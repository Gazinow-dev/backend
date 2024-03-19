package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.*;
import com.gazi.gazi_renew.dto.IssueResponse;
import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.MyFindRoadResponse;
import com.gazi.gazi_renew.dto.MyFindRoadResponse.SubPath;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.*;
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
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            List<MyFindRoadPath> myFindRoadPaths = myFindRoadPathRepository.findAllByMemberOrderByIdDesc(member);
            List<MyFindRoadResponse> myFindRoadResponses = new ArrayList<>();
            for (MyFindRoadPath myFindRoadPath : myFindRoadPaths) {

                //서브패스를 찾는다.
                List<MyFindRoadSubPath> myFindRoadSubPaths = myFindRoadSubPathRepository.findAllByMyFindRoadPath(myFindRoadPath);
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
                        MyFindRoadLane myFindRoadLane = myFindRoadLaneRepository.findByMyFindRoadSubPath(subPath).orElseThrow(() -> new EntityNotFoundException("lane이 존재하지 않습니다."));
                        List<MyFindRoadStation> myFindRoadStations = myFindRoadSubwayRepository.findAllByMyFindRoadSubPath(subPath);

                        // response로 가공
                        String lineName =  myFindRoadLane.getName();
                        // line entity
                        Line line = lineRepository.findByLineName(lineName).orElseThrow(
                                () -> new EntityNotFoundException("호선으로된 데이터 정보를 찾을 수 없습니다.")
                        );
//                        List<IssueResponse.IssueSummaryDto> issueSummaryDtos = IssueResponse.IssueSummaryDto.getIssueSummaryDto(issueService.getIssuesByLine(lineName));
                        ArrayList<MyFindRoadResponse.Lane> lanes = new ArrayList<>();
                        MyFindRoadResponse.Lane lane = MyFindRoadResponse.Lane.builder()
                                .name(myFindRoadLane.getName())
                                .startName(myFindRoadLane.getStartName())
                                .endName(myFindRoadLane.getEndName())
                                .stationCode(myFindRoadLane.getStationCode())
//                                .issueSummary(issueSummaryDtos)
                                .build();
                        lanes.add(lane);

                        List<IssueResponse.IssueSummaryDto> issueDtoList = new ArrayList<>();
                        ArrayList<MyFindRoadResponse.Station> stations = new ArrayList<>();

                        for (MyFindRoadStation myFindRoadStation : myFindRoadStations) {
                            Station stationEntity = subwayDataService.getStationByNameAndLine(myFindRoadStation.getStationName(),lineName);
                            List<Issue> issues = stationEntity.getIssues();
                            List<Issue> activeIssues = new ArrayList<>();
                            // activeIssues에 issues 중에서 issue.getExpireDate값이 현재시간보다 앞서는 값만 받도록 설계
                            LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시간

                            for (Issue issue : issues) {
                                if (issue.getExpireDate() != null && issue.getExpireDate().isAfter(currentDateTime)) {
                                    activeIssues.add(issue);
                                }
                            }
                            List<IssueResponse.IssueSummaryDto> issueSummaryDtos =IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssues);
                            MyFindRoadResponse.Station station = MyFindRoadResponse.Station.builder()
                                    .stationName(myFindRoadStation.getStationName())
                                    .index(myFindRoadStation.getIndex())
                                    .issueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssues))
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
                        .id(myFindRoadPath.getId())
                        .roadName(myFindRoadPath.getName())
                        .lastEndStation(myFindRoadPath.getLastEndStation())
                        .totalTime(myFindRoadPath.getTotalTime())
                        .subPaths(subPaths)
                        .build();
                myFindRoadResponses.add(myFindRoadResponse);
            }
            return response.success(myFindRoadResponses, "마이 길찾기 조회 성공", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public ResponseEntity<Response.Body> getRoute(Long id) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            MyFindRoadPath myFindRoadPath = myFindRoadPathRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당하는 길찾기가 존재하지 않습니다."));


            MyFindRoadResponse myFindRoadResponse = MyFindRoadResponse.builder()
                    .roadName(myFindRoadPath.getName())
                    .transitStations(subwayDataService.getTransitStation(myFindRoadPath))
                    //todo: issue는 추후에
                    .build();

            return response.success("개별조회");
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public ResponseEntity<Response.Body> addRoute(MyFindRoadRequest request) {
        log.info("길저장 서비스 로직 진입");
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            MyFindRoadPath myFindRoadPath = MyFindRoadPath.builder()
                    .member(member)
                    .name(request.getRoadName())
                    .totalTime(request.getTotalTime())
                    .firstStartStation(request.getFirstStartStation())
                    .lastEndStation(request.getLastEndStation())
                    .stationTransitCount(request.getStationTransitCount())
                    .build();

            if (myFindRoadPathRepository.existsByNameAndMember(request.getRoadName(), member)) {
                return response.fail("이미 존재하는 이름입니다.", HttpStatus.CONFLICT);
            }

            myFindRoadPathRepository.save(myFindRoadPath);
            log.info("myFindRoadPath 저장");
            for (MyFindRoadRequest.SubPath subPath : request.getSubPaths()) {
                MyFindRoadSubPath myFindRoadSubPath = MyFindRoadSubPath.builder()
                        .myFindRoadPath(myFindRoadPath)
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
                    MyFindRoadLane myFindRoadLane = MyFindRoadLane.builder()
                            .myFindRoadSubPath(myFindRoadSubPath)
                            .name(lain.getName())
                            .stationCode(lain.getStationCode())
                            .startName(lain.getStartName())
                            .endName(lain.getEndName())
                            .build();
                    myFindRoadLaneRepository.save(myFindRoadLane);
                    log.info("MyFindRoadLane 저장 완료");
                }

                for (MyFindRoadRequest.Station station : subPath.getStations()) {
                    MyFindRoadStation myFindRoadStation = MyFindRoadStation.builder()
                            .myFindRoadSubPath(myFindRoadSubPath)
                            .index(station.getIndex())
                            .stationName(station.getStationName())
                            .build();
                    myFindRoadSubwayRepository.save(myFindRoadStation);
                    log.info("MyFindRoadSubway 저장 완료");
                }


            }
            return response.success(myFindRoadPath.getId(), "데이터 저장완료", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Response.Body> deleteRoute(Long id) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
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
}
