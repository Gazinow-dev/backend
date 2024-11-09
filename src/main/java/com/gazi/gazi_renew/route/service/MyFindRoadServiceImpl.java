package com.gazi.gazi_renew.route.service;

import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.service.port.MyFindRoadLaneRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long addRoute(MyFindRoadCreate myFindRoadCreate) {
        log.info("길저장 서비스 로직 진입");
        Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, member);

        if (myFindRoadPathRepository.existsByNameAndMember(myFindRoadCreate.getRoadName(), member)) {
            throw ErrorCode.throwDuplicateRoadName();
        }
        myFindRoadPathRepository.save(myFindRoad);
        log.info("myFindRoadPath 저장");
        for (MyFindRoadSubPath myFindRoadSubPath : myFindRoadCreate.getMyFindRoadSubPaths()) {
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
}
