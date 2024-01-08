package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.*;
import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyFindRoadServiceImpl implements MyFindRoadService {

    private final Response response;
    private final MemberRepository memberRepository;
    private final MyFindRoadRepository myFindRoadRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
    private final MyFindRoadLaneRepository myFindRoadLaneRepository;
    private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;

    //회원 인증하고

    @Override
    public ResponseEntity<Response.Body> addRoute(MyFindRoadRequest request) {
        log.info("길저장 서비스 로직 진입");
        Member member =  memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .name(request.getName())
                .member(member).build();
        myFindRoadRepository.save(myFindRoad);

        for(MyFindRoadRequest.Path path : request.getPaths()){
            MyFindRoadPath myFindRoadPath = MyFindRoadPath.builder()
                    .myFindRoad(myFindRoad)
                    .totalTime(path.getTotalTime())
                    .firstStartStation(path.getFirstStartStation())
                    .lastEndStation(path.getLastEndStation())
                    .subwayTransitCount(path.getSubwayTransitCount())
                    .build();
            myFindRoadPathRepository.save(myFindRoadPath);
            log.info("myFindRoadPath 저장");
            for(MyFindRoadRequest.SubPath subPath : path.getSubPaths()){
                MyFindRoadSubPath myFindRoadSubPath = MyFindRoadSubPath.builder()
                        .myFindRoadPath(myFindRoadPath)
                        .distance(subPath.getDistance())
                        .sectionTime(subPath.getSectionTime())
                        .stationCount(subPath.getStationCount())
                        .trafficType(subPath.getTrafficType())
                        .build();
                myFindRoadSubPathRepository.save(myFindRoadSubPath);
                log.info("myFindRoadSubPath 저장");
                for (MyFindRoadRequest.Lane lain : subPath.getLanes()){
                    MyFindRoadLane myFindRoadLane = MyFindRoadLane.builder()
                            .myFindRoadSubPath(myFindRoadSubPath)
                            .name(lain.getName())
                            .subwayCode(lain.getSubwayCode())
                            .startName(lain.getStartName())
                            .endName(lain.getEndName())
                            .build();
                    myFindRoadLaneRepository.save(myFindRoadLane);
                    log.info("MyFindRoadLane 저장 완료");
                }

                for (MyFindRoadRequest.Subway subway : subPath.getSubways()){
                    MyFindRoadSubway myFindRoadSubway = MyFindRoadSubway.builder()
                            .myFindRoadSubPath(myFindRoadSubPath)
                            .index(subway.getIndex())
                            .stationName(subway.getStationName())
                            .build();
                    myFindRoadSubwayRepository.save(myFindRoadSubway);
                    log.info("MyFindRoadSubway 저장 완료");
                }
            }
        }

        return response.success(request.getName(),"데이터 저장완료", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Response.Body> deleteRoute(Long id)
    {
        try{
            Member member =  memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(()
                    -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        }catch (EntityNotFoundException e){
            return response.fail(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }

        if(myFindRoadRepository.existsById(id)){
            myFindRoadRepository.deleteById(id);
            return response.success("삭제 완료");
        }else{
            return response.fail("해당 id로 존재하는 MyFindRoad가 없습니다.",HttpStatus.BAD_REQUEST);
        }
    }
}
