package com.gazi.gazi_renew.route.service;

import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.common.exception.MyFindRoadCustomException;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.*;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import com.gazi.gazi_renew.station.domain.Station;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MyFindRoadServiceImplTest {
    private MyFindRoadServiceImpl myFindRoadServiceImpl;
    @BeforeEach
    void init() {
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();

        FakeMyFindRoadPathRepository fakeMyFindRoadPathRepository = new FakeMyFindRoadPathRepository();

        FakeMyFindRoadSubPathRepository fakeMyFindRoadSubPathRepository = new FakeMyFindRoadSubPathRepository();
        FakeMyFindRoadSubwayRepository fakeMyFindRoadSubwayRepository = new FakeMyFindRoadSubwayRepository();
        FakeSubwayRepository fakeSubwayRepository = new FakeSubwayRepository();

        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();
        FakeIssueRepository fakeIssueRepository = new FakeIssueRepository();
        FakeIssueStationRepository fakeIssueStationRepository = new FakeIssueStationRepository();

        this.myFindRoadServiceImpl = MyFindRoadServiceImpl.builder()
                .memberRepository(fakeMemberRepository)
                .myFindRoadPathRepository(fakeMyFindRoadPathRepository)
                .myFindRoadSubPathRepository(fakeMyFindRoadSubPathRepository)
                .myFindRoadSubwayRepository(fakeMyFindRoadSubwayRepository)
                .subwayRepository(fakeSubwayRepository)
                .issueRepository(fakeIssueRepository)
                .securityUtilService(fakeSecurityUtil)
                .issueStationRepository(fakeIssueStationRepository)
                .build();

        Member member1 = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("encoded_tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(false)
                .mySavedRouteNotificationEnabled(false)
                .routeDetailNotificationEnabled(false)
                .firebaseToken("firebaseToken")
                .build();

        fakeSecurityUtil.addEmail("mw310@naver.com");
        fakeMemberRepository.save(member1);
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("삼각지")
                .stationCode(2)
                .lat(37.535534)
                .lng(126.974032)
                .issueStationCode(2)
                .build();
        Station station3 = Station.builder()
                .id(3L)
                .line("수도권 6호선")
                .name("녹사평")
                .stationCode(3)
                .lat(37.535534)
                .lng(126.974032)
                .issueStationCode(3)
                .build();
        fakeSubwayRepository.save(station1);
        fakeSubwayRepository.save(station2);
        fakeSubwayRepository.save(station3);

        MyFindRoadStation myFindRoadStation1 = MyFindRoadStation.builder()
                .id(1L)
                .index(0)
                .stationName("효창공원앞")
                .myFindRoadSubPathId(1L)
                .build();
        MyFindRoadStation myFindRoadStation2 = MyFindRoadStation.builder()
                .id(2L)
                .index(1)
                .stationName("삼각지")
                .myFindRoadSubPathId(1L)
                .build();
        List<MyFindRoadStation> myFindRoadStationList = Arrays.asList(myFindRoadStation2,myFindRoadStation1);

        MyFindRoadSubPath subPath = MyFindRoadSubPath.builder()
                .id(1L)
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("삼각지")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(myFindRoadStationList)
                .build();

        fakeMyFindRoadSubwayRepository.save(myFindRoadStation1);
        fakeMyFindRoadSubwayRepository.save(myFindRoadStation2);

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .memberId(1L)
                .build();
        subPath = MyFindRoadSubPath.builder()
                .id(1L)
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("삼각지")
                .door("null")
                .name("수도권 6호선")
                .myFindRoad(myFindRoad)
                .stationCode(6)
                .stations(myFindRoadStationList)
                .build();

        fakeMyFindRoadSubPathRepository.save(subPath);
        fakeMyFindRoadPathRepository.save(myFindRoad);
    }
    @Test
    void getRoutes는_나의_자주_찾는_경로를_찾을_수_있다() throws Exception{
        //given
        //when
        List<MyFindRoad> myFindRoadList = myFindRoadServiceImpl.getRoutes();
        //then
        assertThat(myFindRoadList.size()).isEqualTo(1);
        assertThat(myFindRoadList.get(0).getRoadName()).isEqualTo("민우테스트");
        assertThat(myFindRoadList.get(0).getFirstStartStation()).isEqualTo("효창공원앞");
        assertThat(myFindRoadList.get(0).getLastEndStation()).isEqualTo("삼각지");
    }
    @Test
    void getRoutesByMember는_memberId를_통해_내가_저장한_경로들을_찾을_수_있다() throws Exception{
        //given
        Long memberId = 1L;
        //when
        List<MyFindRoad> myFindRoadList = myFindRoadServiceImpl.getRoutesByMember(memberId);
        //then
        assertThat(myFindRoadList.size()).isEqualTo(1);
        assertThat(myFindRoadList.get(0).getRoadName()).isEqualTo("민우테스트");
    }
    @Test
    void getRouteById를_통해_id값으로_내가_저장한_경로를_찾을_수_있다() throws Exception{
        //given
        Long id = 552L;
        //when
        MyFindRoad myFindRoad = myFindRoadServiceImpl.getRouteById(id);
        //then
        assertThat(myFindRoad.getRoadName()).isEqualTo("민우테스트");
        assertThat(myFindRoad.getId()).isEqualTo(id);
        assertThat(myFindRoad.getFirstStartStation()).isEqualTo("효창공원앞");
    }
    @Test
    void getRouteById는_잘못된_id값을_받으면_예외를_터트린다() throws Exception{
        //given
        Long id = 23L;
        //when
        assertThatThrownBy(() -> myFindRoadServiceImpl.getRouteById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
    @Test
    void addRoute는_새로운_나의_경로를_저장할_수_있다() throws Exception{
        //given
        MyFindRoadSubPathCreate subPathCreate = MyFindRoadSubPathCreate.builder()
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("녹사팡")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(Arrays.asList(
                        MyFindRoadStationCreate.builder().index(0).stationName("삼각지").build(),
                        MyFindRoadStationCreate.builder().index(1).stationName("녹사평").build()
                ))
                .build();
        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("가는길 지금 경로")
                .totalTime(12)
                .stationTransitCount(1)
                .firstStartStation("삼각지")
                .lastEndStation("녹사평")
                .subPaths(Arrays.asList(subPathCreate))
                .build();
        //when
        Long myFindRoadId = myFindRoadServiceImpl.addRoute(myFindRoadCreate);
        //then
        assertThat(myFindRoadId).isEqualTo(1L);
    }
    @Test
    void addRoute는_이미_있는_경로_이름을_저장하면_예외를_터트린다() throws Exception{
        //given
        MyFindRoadSubPathCreate subPathCreate = MyFindRoadSubPathCreate.builder()
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("녹사평")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(Arrays.asList(
                        MyFindRoadStationCreate.builder().index(0).stationName("삼각지").build(),
                        MyFindRoadStationCreate.builder().index(1).stationName("녹사평").build()
                ))
                .build();
        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("민우테스트")
                .totalTime(12)
                .stationTransitCount(1)
                .firstStartStation("삼각지")
                .lastEndStation("녹사평")
                .subPaths(Arrays.asList(subPathCreate))
                .build();
        //when
        assertThatThrownBy(() -> myFindRoadServiceImpl.addRoute(myFindRoadCreate)).isInstanceOf(MyFindRoadCustomException.class);
    }

    @Test
    void deleteRoute는_존재하지_않는_경로의_id가_들어올_경우_예외를_터트린다() throws Exception{
        //given
        Long myFindRoadId = 9999L;
        //when
        assertThatThrownBy(() -> myFindRoadServiceImpl.deleteRoute(myFindRoadId)).isInstanceOf(CustomException.class);
    }
    @Test
    void updateRouteNotification는_각각의_경로_푸시_알림_설정을_업데이트할_수_있다() throws Exception{
        //given
        Long myFindRoadId = 552L;
        Boolean enabled = true;
        //when
        myFindRoadServiceImpl.updateRouteNotification(myFindRoadId, enabled);
        MyFindRoad myFindRoad = myFindRoadServiceImpl.getRouteById(myFindRoadId);
        //then
        assertThat(myFindRoad.getId()).isEqualTo(552L);
        assertThat(myFindRoad.getNotification()).isTrue();
    }
}