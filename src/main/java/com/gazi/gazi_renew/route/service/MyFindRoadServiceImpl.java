package com.gazi.gazi_renew.route.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.common.exception.MyFindRoadErrorCode;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Builder
@Transactional
@RequiredArgsConstructor
public class MyFindRoadServiceImpl implements MyFindRoadService {

    private final MemberRepository memberRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
    private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;
    private final NotificationRepository notificationRepository;
    private final SubwayRepository subwayRepository;
    private final IssueRepository issueRepository;
    private final SecurityUtilService securityUtilService;
    private final IssueStationRepository issueStationRepository;

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

        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, member.getId());

        if (myFindRoadPathRepository.existsByNameAndMember(myFindRoadCreate.getRoadName(), member)) {
            throw MyFindRoadErrorCode.throwDuplicateRoadName();
        }
        if (myFindRoadPathRepository.existsByFirstStartStationAndLastEndStationAndMember(myFindRoad.getFirstStartStation(), myFindRoad.getLastEndStation(), member)){
            throw MyFindRoadErrorCode.throwDuplicateRoadPath();
        }
        log.info("myFindRoadPath 저장");
        myFindRoad = myFindRoadPathRepository.save(myFindRoad);

        for (MyFindRoadSubPathCreate myFindRoadSubPathCreate : myFindRoadCreate.getSubPaths()) {
            MyFindRoadSubPath myFindRoadSubPath = MyFindRoadSubPath.from(myFindRoadSubPathCreate, myFindRoad);
            myFindRoadSubPath = myFindRoadSubPathRepository.save(myFindRoadSubPath);
            log.info("myFindRoadSubPath 저장");
            for (MyFindRoadStationCreate myFindRoadStationCreate : myFindRoadSubPathCreate.getStations()) {
                MyFindRoadStation myFindRoadStation = MyFindRoadStation.from(myFindRoadStationCreate, myFindRoadSubPath.getId());
                myFindRoadSubwayRepository.save(myFindRoadStation);
                log.info("MyFindRoadSubway 저장 완료");
            }
        }
        //초기 알림 생성
        notificationRepository.saveAll(Notification.initNotification(myFindRoad.getId()));

        return myFindRoad.getId();
        }
    @Override
    public void deleteRoute(Long id) {
        if (myFindRoadPathRepository.existsById(id)) {
            // 자식 엔티티(MyFindRoadSubPath) 조회
            List<MyFindRoadSubPath> myFindRoadSubPathList = myFindRoadSubPathRepository.findByMyFindRoadPathId(id);
            for (MyFindRoadSubPath myFindRoadSubPath : myFindRoadSubPathList) {
                // 손자 엔티티(MyFindRoadStation) 삭제
                List<MyFindRoadStation> myFindRoadStationList = myFindRoadSubwayRepository.findAllByMyFindRoadSubPathId(myFindRoadSubPath.getId());
                myFindRoadSubwayRepository.deleteAll(myFindRoadStationList);
            }
            myFindRoadSubPathRepository.deleteAll(myFindRoadSubPathList);

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
        // false 일때 , 멤버의 경로 true가 1개밖에 없으면 나의 경로 알림 설정 비활성화
        if (!enabled) {
            enableMySavedRouteNotificationIfAllRoutesDisabled(myFindRoad);
        }
        myFindRoadPathRepository.updateNotification(myFindRoad);
    }

    private void enableMySavedRouteNotificationIfAllRoutesDisabled(MyFindRoad myFindRoad) {
        // 사용자의 저장한 경로 중 알림이 활성화된 개수를 조회
        int enabledCount = myFindRoadPathRepository.countEnabledNotificationByMemberId(myFindRoad.getMemberId());
        // 저장된 경로 중 하나라도 활성화된 알림이 있는지 확인
        if (enabledCount == 1) {
            Member member = memberRepository.findById(myFindRoad.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("멤버가 존재하지 않습니다: "));
            // "내가 저장한 경로 알림 설정" 비활성화
            Member newMember = member.updateMySavedRouteNotificationEnabled(false);
            memberRepository.updateAlertAgree(newMember);
        }
    }

    private List<MyFindRoad> getStationList(List<MyFindRoad> myFindRoadList) {
        List<MyFindRoad> updatedRoadList = new ArrayList<>();

        for (MyFindRoad myFindRoad : myFindRoadList) {
            List<MyFindRoadSubPath> updatedSubPaths = new ArrayList<>();

            List<MyFindRoadSubPath> myFindRoadSubPathList = myFindRoadSubPathRepository.findByMyFindRoadPathId(myFindRoad.getId());

            for (MyFindRoadSubPath myFindRoadSubPath : myFindRoadSubPathList) {

                List<MyFindRoadStation> updatedStations = new ArrayList<>();
                // myFindRoadSubPathEntity에는 저장을 안해서 따로 조회(간접 참조)만 사용중
                List<MyFindRoadStation> myFindRoadStationList = myFindRoadSubwayRepository.findAllByMyFindRoadSubPathId(myFindRoadSubPath.getId());
                for (MyFindRoadStation myFindRoadStation : myFindRoadStationList) {
                    String line = myFindRoadSubPath.getName();
                    if(line.equals("수도권 9호선(급행)")){
                        line = "수도권 9호선";
                    }
                    List<Station> stationList = subwayRepository.findByNameContainingAndLine(myFindRoadStation.getStationName(), line);
                    Station station = Station.toFirstStation(myFindRoadStation.getStationName(), stationList);

                    if (station != null) {
                        List<IssueStation> issueStationList = issueStationRepository.findAllByStationId(station.getId());
                        List<Issue> issueList = issueStationList.stream().map(IssueStation::getIssue)
                                .collect(Collectors.toList());
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
