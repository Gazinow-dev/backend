package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLaneCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
                .lanes(Collections.singletonList(MyFindRoadLaneCreate.builder()
                        .name("수도권 6호선")
                        .stationCode(6)
                        .build()))
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
                .routeDetailNotificationEnabled(true)
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
        MyFindRoad myFindRoad = MyFindRoad.from(myFindRoadCreate, member);
        //then
        assertThat(myFindRoad.getRoadName()).isEqualTo("민우 출근길");
        assertThat(myFindRoad.getMember().getNickName()).isEqualTo("minu");
        assertThat(myFindRoad.getFirstStartStation()).isEqualTo("효창공원앞");
        assertThat(myFindRoad.getLastEndStation()).isEqualTo("삼각지");
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
                .lanes(Collections.singletonList(MyFindRoadLane.builder()
                        .name("수도권 6호선")
                        .stationCode(6)
                        .build()))
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
                .lanes(Collections.singletonList(MyFindRoadLane.builder()
                        .name("수도권 6호선")
                        .stationCode(6)
                        .build()))
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