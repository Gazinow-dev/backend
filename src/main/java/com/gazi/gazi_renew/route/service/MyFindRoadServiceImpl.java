package com.gazi.gazi_renew.route.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.service.port.MyFindRoadLaneRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.StationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MyFindRoadServiceImpl implements MyFindRoadService {

    private final MemberRepository memberRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
    private final MyFindRoadLaneRepository myFindRoadLaneRepository;
    private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;
    private final StationService stationService;
    private final IssueRepository issueRepository;
    private final SecurityUtilService securityUtilService;


    @Override
    @Transactional(readOnly = true)
    public  List<MyFindRoad> getRoutes() {
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        List<MyFindRoad> myFindRoadList = myFindRoadPathRepository.findAllByMemberOrderByIdDesc(member);
        return getStationList(myFindRoadList);
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
    public Long addRoute(MyFindRoadCreate myFindRoadCreate) {
        log.info("길저장 서비스 로직 진입");
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, member);

        if (myFindRoadPathRepository.existsByNameAndMember(myFindRoadCreate.getRoadName(), member)) {
            throw ErrorCode.throwDuplicateRoadName();
        }
        myFindRoadPathRepository.save(myFindRoad);
        log.info("myFindRoadPath 저장");

        for (MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getSubPaths()) {
            myFindRoadSubPathRepository.save(myFindRoadSubPath);
            log.info("myFindRoadSubPath 저장");
            for (MyFindRoadLane myFindRoadLane: myFindRoadSubPath.getLanes()) {
                myFindRoadLaneRepository.save(myFindRoadLane);
                log.info("MyFindRoadLane 저장 완료");
            }
            for (MyFindRoadStation myFindRoadStation : myFindRoadSubPath.getStations()) {

                myFindRoadSubwayRepository.save(myFindRoadStation, myFindRoadSubPath);
                log.info("MyFindRoadSubway 저장 완료");
            }
        }
        return myFindRoad.getId();
        }
    @Override
    public void deleteRoute(Long id) {
        if (myFindRoadPathRepository.existsById(id)) {
            myFindRoadPathRepository.deleteById(id);
        } else {
            throw ErrorCode.throwMyFindRoadNotFoundException();
        }
    }
    @Override
    public void updateRouteNotification(Long id, Boolean enabled) {
        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(id)
                .orElseThrow(ErrorCode::throwMyFindRoadNotFoundException);

        myFindRoad = myFindRoad.updateNotification(enabled);
        myFindRoadPathRepository.save(myFindRoad);
    }
    private List<MyFindRoad> getStationList(List<MyFindRoad> myFindRoadList) {
        List<MyFindRoad> updatedRoadList = new ArrayList<>();

        for (MyFindRoad myFindRoad : myFindRoadList) {
            List<MyFindRoadSubPath> updatedSubPaths = new ArrayList<>();

            for (MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getSubPaths()) {
                MyFindRoadLane myFindRoadLane = myFindRoadLaneRepository.findByMyFindRoadSubPath(myFindRoadSubPath)
                        .orElseThrow(() -> new EntityNotFoundException("lane이 존재하지 않습니다."));

                List<MyFindRoadStation> updatedStations = new ArrayList<>();

                for (MyFindRoadStation myFindRoadStation : myFindRoadSubwayRepository.findAllByMyFindRoadSubPath(myFindRoadSubPath)) {
                    Station station = stationService.getStationByNameAndLine(myFindRoadStation.getStationName(), myFindRoadLane.getName());
                    if (station != null) {
                        List<Issue> issueList = issueRepository.findByStationId(station.getId());
                        myFindRoadStation = myFindRoadStation.updateIssueList(issueList);  // 업데이트된 station 객체 생성
                    }
                    updatedStations.add(myFindRoadStation);  // 변경된 객체를 리스트에 추가
                }

                MyFindRoadSubPath updatedSubPath = myFindRoadSubPath.updateStations(updatedStations);  // 변경된 stations 반영
                updatedSubPaths.add(updatedSubPath);  // 변경된 subPath 리스트에 추가
            }

            MyFindRoad updatedRoad = myFindRoad.updateSubPaths(updatedSubPaths);  // 변경된 subPaths 반영
            updatedRoadList.add(updatedRoad);  // 변경된 road 리스트에 추가
        }

        return updatedRoadList;
    }


}
