package com.gazi.gazi_renew.route.service;

import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.infrastructure.*;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadSubwayRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadLaneRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import com.gazi.gazi_renew.station.service.StationService;
import com.gazi.gazi_renew.station.infrastructure.LineRepository;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final StationService stationService;
    //회원 인증하고

    @Override
    @Transactional(readOnly = true)
    public  List<MyFindRoad> getRoutes() {
        Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        List<MyFindRoad> myFindRoadList = myFindRoadPathRepository.findAllByMemberOrderByIdDesc(member);
        return myFindRoadList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyFindRoad> getRoutesByMember(Long memberId) {
        List<MyFindRoad> myFindRoadList = myFindRoadPathRepository.findByMemberId(memberId);
        return myFindRoadList;
    }

    @Transactional(readOnly = true)
    public MyFindRoad getRouteById(Long id) {
        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id로 마이길찾기 데이터 정보를 찾을 수 없습니다."));
        return myFindRoad;
    }


        @Override
    public ResponseEntity<Response.Body> addRoute(MyFindRoadCreate myFindRoadCreate) {
        log.info("길저장 서비스 로직 진입");
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

//            MyFindRoad myFindRoad = MyFindRoadPathEntity.builder()
//                    .memberEntity(memberEntity)
//                    .name(myFindRoadCreate.getRoadName())
//                    .totalTime(myFindRoadCreate.getTotalTime())
//                    .firstStartStation(myFindRoadCreate.getFirstStartStation())
//                    .lastEndStation(myFindRoadCreate.getLastEndStation())
//                    .stationTransitCount(myFindRoadCreate.getStationTransitCount())
//                    .notification(false)
//                    .build();

            if (myFindRoadPathRepository.existsByNameAndMember(myFindRoadCreate.getRoadName(), member)) {
                return response.fail("이미 존재하는 이름입니다.", HttpStatus.CONFLICT);
            }

            myFindRoadPathRepository.save(myFindRoad);
            log.info("myFindRoadPath 저장");
            for (MyFindRoad.SubPath subPath : myFindRoadCreate.getSubPaths()) {
                MyFindRoadSubPathEntity myFindRoadSubPathEntity = MyFindRoadSubPathEntity.builder()
                        .myFindRoadPathEntity(myFindRoad)
                        .distance(subPath.getDistance())
                        .sectionTime(subPath.getSectionTime())
                        .stationCount(subPath.getStationCount())
                        .trafficType(subPath.getTrafficType())
                        .door(subPath.getDoor())
                        .way(subPath.getWay())
                        .build();
                myFindRoadSubPathRepository.save(myFindRoadSubPathEntity);
                log.info("myFindRoadSubPath 저장");
                for (MyFindRoad.Lane lain : subPath.getLanes()) {
                    MyFindRoadLaneEntity myFindRoadLaneEntity = MyFindRoadLaneEntity.builder()
                            .myFindRoadSubPathEntity(myFindRoadSubPathEntity)
                            .name(lain.getName())
                            .stationCode(lain.getStationCode())
                            .startName(lain.getStartName())
                            .endName(lain.getEndName())
                            .build();
                    myFindRoadLaneRepository.save(myFindRoadLaneEntity);
                    log.info("MyFindRoadLane 저장 완료");
                }

                for (MyFindRoad.Station station : subPath.getStations()) {
                    MyFindRoadStationEntity myFindRoadStationEntity = MyFindRoadStationEntity.builder()
                            .myFindRoadSubPathEntity(myFindRoadSubPathEntity)
                            .index(station.getIndex())
                            .stationName(station.getStationName())
                            .build();
                    myFindRoadSubwayRepository.save(myFindRoadStationEntity);
                    log.info("MyFindRoadSubway 저장 완료");
                }


            }
            return response.success(myFindRoad.getId(), "데이터 저장완료", HttpStatus.CREATED);
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
