package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

class MyFindRoadTest {
    @Test
    void MyFindRoad는_MyFindRoadCreate_객체로_생성할_수_있다() throws Exception{
        //given
        MyFindRoadSubPathCreate subPathCreate = MyFindRoadSubPathCreate.builder()
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("삼각지")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(Arrays.asList(
                        MyFindRoadStationCreate.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStationCreate.builder().index(1).stationName("삼각지").build()
                ))
                .build();
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();

        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .subPaths(Arrays.asList(subPathCreate))
                .build();
        //when
        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, member.getId());
        //then
        assertThat(myFindRoad.getRoadName()).isEqualTo("민우 출근길");
        assertThat(myFindRoad.getFirstStartStation()).isEqualTo("효창공원앞");
        assertThat(myFindRoad.getLastEndStation()).isEqualTo("삼각지");
    }
    @Test
    void 출발역_도보_시간을_입력하지_않으면_기본값_5분이_적용된다() throws Exception {
        // given
        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .subPaths(Arrays.asList())
                .build();
        // when
        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, 1L);
        // then
        assertThat(myFindRoad.getWalkingTimeFromStartStation()).isEqualTo(5);
    }

    @Test
    void 출발역_도보_시간을_직접_입력하면_해당_값이_적용된다() throws Exception {
        // given
        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .walkingTimeFromStartStation(10)
                .subPaths(Arrays.asList())
                .build();
        // when
        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, 1L);
        // then
        assertThat(myFindRoad.getWalkingTimeFromStartStation()).isEqualTo(10);
    }

    @Test
    void 도착역_도보_시간을_입력하지_않으면_null이다() throws Exception {
        // given
        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .subPaths(Arrays.asList())
                .build();
        // when
        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, 1L);
        // then
        assertThat(myFindRoad.getWalkingTimeToEndStation()).isNull();
    }

    @Test
    void 도착역_도보_시간을_직접_입력하면_해당_값이_적용된다() throws Exception {
        // given
        MyFindRoadCreate myFindRoadCreate = MyFindRoadCreate.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .walkingTimeToEndStation(7)
                .subPaths(Arrays.asList())
                .build();
        // when
        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, 1L);
        // then
        assertThat(myFindRoad.getWalkingTimeToEndStation()).isEqualTo(7);
    }

    @Test
    void 알림_설정_변경_시_도보_시간이_유지된다() throws Exception {
        // given
        MyFindRoad myFindRoad = MyFindRoad.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .walkingTimeFromStartStation(8)
                .walkingTimeToEndStation(3)
                .notification(true)
                .build();
        // when
        myFindRoad = myFindRoad.updateNotification(false);
        // then
        assertThat(myFindRoad.getWalkingTimeFromStartStation()).isEqualTo(8);
        assertThat(myFindRoad.getWalkingTimeToEndStation()).isEqualTo(3);
    }

    @Test
    void MyFindRoad는_알림_설정을_활성화_및_비활성화할_수_있다() throws Exception{
        //given
        MyFindRoad myFindRoad = MyFindRoad.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .notification(true)
                .build();
        //when
        myFindRoad = myFindRoad.updateNotification(false);
        //then
        assertThat(myFindRoad.getNotification()).isFalse();
    }
    @Test
    void MyFindRoad는_SubPaths를_업데이트_할_수_있다() throws Exception{
        //given
        MyFindRoadSubPath subPath = MyFindRoadSubPath.builder()
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("삼각지")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(Arrays.asList(
                        MyFindRoadStation.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStation.builder().index(1).stationName("삼각지").build()
                ))
                .build();
        MyFindRoadSubPath updateSubpath = MyFindRoadSubPath.builder()
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(2)
                .way("녹사평")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(Arrays.asList(
                        MyFindRoadStation.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStation.builder().index(1).stationName("삼각지").build(),
                        MyFindRoadStation.builder().index(2).stationName("녹사평").build()
                ))
                .build();
        MyFindRoad myFindRoad = MyFindRoad.builder()
                .roadName("민우 출근길")
                .totalTime(30)
                .stationTransitCount(0)
                .firstStartStation("효창공원앞")
                .lastEndStation("삼각지")
                .subPaths(Arrays.asList(subPath))
                .notification(true)
                .build();
        //when
        myFindRoad = myFindRoad.updateSubPaths(Arrays.asList(updateSubpath));
        //then
        assertThat(myFindRoad.getSubPaths().get(0).getWay()).isEqualTo("녹사평");
        assertThat(myFindRoad.getSubPaths().get(0).getStations().size()).isEqualTo(3);

    }
}