package com.gazi.gazi_renew.notification.domain;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationTest {
    @Test
    void MyFindRoadNotificationCreate로_Notification을_생성할_수_있다() throws Exception{
        //given
        MyFindRoadNotificationCreate.DayTimeRange dayTimeRange = MyFindRoadNotificationCreate.DayTimeRange
                .builder()
                .day("월")
                .fromTime("14:00")
                .toTime("16:00")
                .build();

        MyFindRoadNotificationCreate myFindRoadNotificationCreate = MyFindRoadNotificationCreate.builder()
                .myPathId(1L)
                .dayTimeRanges(Arrays.asList(dayTimeRange))
                .build();

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


        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(1L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .member(Member.builder()
                        .email("mw310@naver.com")
                        .password("encoded_tempPassword")
                        .nickName("minu")
                        .role(Role.ROLE_USER)
                        .pushNotificationEnabled(false)
                        .mySavedRouteNotificationEnabled(false)
                        .routeDetailNotificationEnabled(false)
                        .firebaseToken("firebaseToken")
                        .build())
                .build();
        //when
        List<Notification> notificationList = Notification.from(myFindRoadNotificationCreate, myFindRoad);
        //then
        assertThat(notificationList.size()).isEqualTo(1);
        assertThat(notificationList.get(0).getFromTime()).isEqualTo("14:00");
        assertThat(notificationList.get(0).getToTime()).isEqualTo("16:00");
    }
    @Test
    void MyFindRoadNotificationCreate로_Notification을_생성할_때_알림_시작시간이_종료시간보다_늦을_수_없다() throws Exception{
        //given
        MyFindRoadNotificationCreate.DayTimeRange dayTimeRange = MyFindRoadNotificationCreate.DayTimeRange
                .builder()
                .day("월")
                .fromTime("16:00") //시작시간이 더 늦음
                .toTime("14:00")
                .build();

        MyFindRoadNotificationCreate myFindRoadNotificationCreate = MyFindRoadNotificationCreate.builder()
                .myPathId(1L)
                .dayTimeRanges(Arrays.asList(dayTimeRange))
                .build();

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


        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(1L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .member(Member.builder()
                        .email("mw310@naver.com")
                        .password("encoded_tempPassword")
                        .nickName("minu")
                        .role(Role.ROLE_USER)
                        .pushNotificationEnabled(false)
                        .mySavedRouteNotificationEnabled(false)
                        .routeDetailNotificationEnabled(false)
                        .firebaseToken("firebaseToken")
                        .build())
                .build();
        //when
        assertThrows(IllegalArgumentException.class, () -> Notification.from(myFindRoadNotificationCreate, myFindRoad));

    }

}