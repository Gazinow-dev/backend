package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.*;
import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.MyFindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private final SubwayDataService subwayDataService;
    //회원 인증하고

    @Override
    public ResponseEntity<Response.Body> getRoutes() {
        try {
            System.out.println(SecurityUtil.getCurrentUserEmail());
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            List<MyFindRoadPath> myFindRoadPaths = myFindRoadPathRepository.findAllByMember(member);
            List<MyFindRoadResponse> myFindRoadResponses = new ArrayList<>();
            for (MyFindRoadPath myFindRoadPath : myFindRoadPaths) {
                MyFindRoadResponse myFindRoadResponse = MyFindRoadResponse.builder()
                        .id(myFindRoadPath.getId())
                        .roadName(myFindRoadPath.getName())
                        .Stations(subwayDataService.getTransitStation(myFindRoadPath))
                        //todo: issue는 추후에
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
                    .Stations(subwayDataService.getTransitStation(myFindRoadPath))
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
            return response.success(myFindRoadPath.getName(), "데이터 저장완료", HttpStatus.CREATED);
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
