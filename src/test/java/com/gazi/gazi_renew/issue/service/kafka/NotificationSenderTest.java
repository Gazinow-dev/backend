package com.gazi.gazi_renew.issue.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.mock.common.FakeRedisUtilServiceImpl;
import com.gazi.gazi_renew.mock.route.FakeMyFindRoadSubPathRepository;
import com.gazi.gazi_renew.mock.route.FakeMyFindRoadSubwayRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class NotificationSenderTest {
    private NotificationSender notificationSender;

    @BeforeEach
    void init() {
        FakeMyFindRoadSubPathRepository fakeMyFindRoadSubPathRepository = new FakeMyFindRoadSubPathRepository();
        FakeRedisUtilServiceImpl fakeRedisUtilService = new FakeRedisUtilServiceImpl(new ObjectMapper());
        FakeMyFindRoadSubwayRepository fakeMyFindRoadSubwayRepository = new FakeMyFindRoadSubwayRepository();

        this.notificationSender = NotificationSender.builder()
                .myFindRoadSubPathRepository(fakeMyFindRoadSubPathRepository)
                .myFindRoadSubwayRepository(fakeMyFindRoadSubwayRepository)
                .redisUtilService(fakeRedisUtilService)
                .build();

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
                .stations(Arrays.asList(
                        MyFindRoadStation.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStation.builder().index(1).stationName("삼각지").build()
                ))
                .build();
        fakeMyFindRoadSubPathRepository.save(subPath);


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

        fakeMyFindRoadSubwayRepository.save(myFindRoadStation1);
        fakeMyFindRoadSubwayRepository.save(myFindRoadStation2);

    }

    @Test
    void matchesRoute는_내_경로의_호선과_이슈의_연관된_호선이_겹칠때만_true를_반환한다() throws Exception{
        //given
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
                .stations(Arrays.asList(
                        MyFindRoadStation.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStation.builder().index(1).stationName("삼각지").build()
                ))
                .build();

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .memberId(1L)
                .build();
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("삼각지")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(2)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(2)
                .build();

        Line line1 = Line.builder()
                .id(1L)
                .lineName("수도권 6호선")
                .build();

        List<Line> lineList = new ArrayList<>(Arrays.asList(line1));
        List<Station> stationList = new ArrayList<>(Arrays.asList(station1, station2));
        //when
        boolean result = notificationSender.matchesRoute(Arrays.asList(subPath), lineList, stationList);
        //then
        Assertions.assertThat(result).isTrue();
    }
    @Test
    void matchesRoute는_내_경로의_호선과_이슈의_연관된_호선이_하나도_겹치지_않으면_false를_반환한다() throws Exception{
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

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .memberId(1L)
                .build();
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("삼각지")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(2)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(2)
                .build();

        Line line1 = Line.builder()
                .id(1L)
                .lineName("수도권 1호선")
                .build();
        Line line2 = Line.builder()
                .id(1L)
                .lineName("수도권 2호선")
                .build();

        List<Line> lineList = new ArrayList<>(Arrays.asList(line1, line2));
        List<Station> stationList = new ArrayList<>(Arrays.asList(station1, station2));
        //when
        boolean result = notificationSender.matchesRoute(Arrays.asList(subPath), lineList, stationList);
        //then
        Assertions.assertThat(result).isFalse();
    }
    @Test
    void matchesRoute는_내_경로의_지하철역과_이슈의_연관된_지하철역이_하나라도_겹치면_true를_반환한다() throws Exception{
        //given
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
                .stations(Arrays.asList(
                        MyFindRoadStation.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStation.builder().index(1).stationName("삼각지").build()
                ))
                .build();

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .memberId(1L)
                .build();
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("삼각지")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("효창공원앞")
                .stationCode(2)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(2)
                .build();

        Line line1 = Line.builder()
                .id(1L)
                .lineName("수도권 1호선")
                .build();
        Line line2 = Line.builder()
                .id(1L)
                .lineName("수도권 6호선")
                .build();

        List<Line> lineList = new ArrayList<>(Arrays.asList(line1, line2));
        List<Station> stationList = new ArrayList<>(Arrays.asList(station1, station2));
        //when
        boolean result = notificationSender.matchesRoute(Arrays.asList(subPath), lineList, stationList);
        //then
        Assertions.assertThat(result).isTrue();
    }
    @Test
    void matchesRoute는_내_경로의_지하철역과_이슈의_연관된_지하철역이_하나라도_겹치지_않으면_false를_반환한다() throws Exception{
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

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .memberId(1L)
                .build();
        Station station1 = Station.builder()
                .id(1L)
                .line("수도권 6호선")
                .name("공덕")
                .stationCode(1)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(1)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .line("수도권 6호선")
                .name("대흥역")
                .stationCode(2)
                .lat(37.539233)
                .lng(126.961384)
                .issueStationCode(2)
                .build();

        Line line1 = Line.builder()
                .id(1L)
                .lineName("수도권 1호선")
                .build();
        Line line2 = Line.builder()
                .id(1L)
                .lineName("수도권 6호선")
                .build();

        List<Line> lineList = new ArrayList<>(Arrays.asList(line1, line2));
        List<Station> stationList = new ArrayList<>(Arrays.asList(station1, station2));
        //when
        boolean result = notificationSender.matchesRoute(Arrays.asList(subPath), lineList, stationList);
        //then
        Assertions.assertThat(result).isFalse();
    }
}