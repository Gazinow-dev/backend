package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.FakeMyFindRoadPathRepository;
import com.gazi.gazi_renew.mock.FakeNotificationRepository;
import com.gazi.gazi_renew.mock.FakeRedisUtilServiceImpl;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationServiceImplTest {
    private NotificationServiceImpl notificationServiceImpl;
    private FakeNotificationRepository fakeNotificationRepository;
    private FakeMyFindRoadPathRepository fakeMyFindRoadPathRepository;
    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();

        fakeNotificationRepository = new FakeNotificationRepository();
        fakeMyFindRoadPathRepository = new FakeMyFindRoadPathRepository();
        FakeRedisUtilServiceImpl fakeRedisUtilService = new FakeRedisUtilServiceImpl(mapper);

        this.notificationServiceImpl = new NotificationServiceImpl(fakeNotificationRepository, fakeMyFindRoadPathRepository
                , fakeRedisUtilService);

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
                        .id(1L)
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
        fakeMyFindRoadPathRepository.save(myFindRoad);

        Notification myFindRoadNotificationCreate = Notification.builder()
                .id(1L)
                .dayOfWeek("월")
                .fromTime(LocalTime.parse("14:00"))
                .toTime(LocalTime.parse("16:00"))
                .myFindRoad(myFindRoad)
                .build();
        fakeNotificationRepository.saveAll(Arrays.asList(myFindRoadNotificationCreate));
    }
    @Test
    void saveNotificationTimes는_알림을_저장할_수_있다() throws Exception{
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
        //when
        List<Notification> notificationList = notificationServiceImpl.saveNotificationTimes(myFindRoadNotificationCreate);
        //then
        assertThat(notificationList.size()).isEqualTo(1);
        assertThat(notificationList.get(0).getDayOfWeek()).isEqualTo("월");
        assertThat(notificationList.get(0).getFromTime()).isEqualTo("14:00");
        assertThat(notificationList.get(0).getToTime()).isEqualTo("16:00");
    }
    @Test
    void saveNotificationTimes에서_알림을_저장하면_MyFindRoad의_알림_상태도_활성화_된다() throws Exception{
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
        //when
        notificationServiceImpl.saveNotificationTimes(myFindRoadNotificationCreate);
        //then
        Optional<MyFindRoad> myFindRoad = fakeMyFindRoadPathRepository.findById(1L);
        assertThat(myFindRoad.get().getNotification()).isTrue();
    }
    @Test
    void getNotificationTimes를_통해_알림_설정을_조회할_수_있다() throws Exception{
        //given
        Long myPathId = 1L;
        //when
        List<Notification> notificationList = notificationServiceImpl.getNotificationTimes(myPathId);
        //then
        assertThat(notificationList.size()).isEqualTo(1);
        assertThat(notificationList.get(0).getId()).isEqualTo(1L);
        assertThat(notificationList.get(0).getDayOfWeek()).isEqualTo("월");
        assertThat(notificationList.get(0).getFromTime()).isEqualTo("14:00");
        assertThat(notificationList.get(0).getToTime()).isEqualTo("16:00");
    }
    @Test
    void deleteNotificationTimes는_알림_설정을_삭제할_수_있다() throws Exception{
        //given
        Long myPathId = 1L;
        //when
        notificationServiceImpl.deleteNotificationTimes(myPathId);
        //then
        List<Notification> resultList = notificationServiceImpl.getNotificationTimes(1L);
        assertThat(resultList.size()).isEqualTo(0);
    }
    @Test
    void updateNotificationTimes는_알림_설정을_업데이트할_수_있다() throws Exception{
        //given
        MyFindRoadNotificationCreate.DayTimeRange dayTimeRange = MyFindRoadNotificationCreate.DayTimeRange
                .builder()
                .day("화") // ⭐️⭐️변경
                .fromTime("15:00") // ⭐️⭐️변경
                .toTime("17:00") // ⭐️⭐️변경
                .build();

        MyFindRoadNotificationCreate myFindRoadNotificationCreate = MyFindRoadNotificationCreate.builder()
                .myPathId(1L)
                .dayTimeRanges(Arrays.asList(dayTimeRange))
                .build();
        //when
        List<Notification> notificationList = notificationServiceImpl.updateNotificationTimes(myFindRoadNotificationCreate);
        //then
        assertThat(notificationList.size()).isEqualTo(1);
        assertThat(notificationList.get(0).getDayOfWeek()).isEqualTo("화");
        assertThat(notificationList.get(0).getFromTime()).isEqualTo("15:00");
        assertThat(notificationList.get(0).getToTime()).isEqualTo("17:00");
    }
    @Test
    void getPathId를_통해_알림_설정의_MyPathId를_조회할_수_있다() throws Exception{
        //given
        Long notificationId = 1L;
        //when
        Long pathId = notificationServiceImpl.getPathId(notificationId);
        //then
        assertThat(pathId).isEqualTo(1L);
    }
}