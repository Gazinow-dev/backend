package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MyFindRoadSubPathTest {
    @Test
    void MyFindRoadSubPath는_MyFindRoadSubPathCreate를_통해_객체를_생성할_수_있다() throws Exception{
        //given
        MyFindRoadStationCreate myFindRoadStationCreate = MyFindRoadStationCreate.builder()
                .index(2)
                .stationName("삼각지")
                .stationCode(1)
                .build();

        MyFindRoadSubPathCreate myFindRoadSubPathCreate = MyFindRoadSubPathCreate.builder()
                .trafficType(0)
                .distance(2)
                .sectionTime(12)
                .stationCount(2)
                .way("녹사평")
                .stations(Arrays.asList(myFindRoadStationCreate))
                .name("수도권 6호선")
                .stationCode(1)
                .build();
        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .firstStartStation("삼각지")
                .lastEndStation("녹사평")
                .notification(false)
                .build();
        //when
        MyFindRoadSubPath myFindRoadSubPath = MyFindRoadSubPath.from(myFindRoadSubPathCreate, myFindRoad);
        //then
        assertThat(myFindRoadSubPath.getStationCount()).isEqualTo(2);
        assertThat(myFindRoadSubPath.getWay()).isEqualTo("녹사평");
    }
    @Test
    void MyFindRoadSubPath는_지하철역을_업데이트할_수_있다() throws Exception{
        //given
        MyFindRoadStation myFindRoadStation = MyFindRoadStation.builder()
                .index(2)
                .stationName("삼각지")
                .build();
        MyFindRoadStation updateStation1 = MyFindRoadStation.builder()
                .index(2)
                .stationName("삼각지")
                .build();
        MyFindRoadStation updateStation2 = MyFindRoadStation.builder()
                .index(3)
                .stationName("녹사평")
                .build();
        List<MyFindRoadStation> updatedStations = Arrays.asList(updateStation1, updateStation2);

        MyFindRoadSubPath myFindRoadSubPath = MyFindRoadSubPath.builder()
                .id(1L)
                .trafficType(0)
                .distance(12)
                .sectionTime(10)
                .stationCount(2)
                .way("녹사평")
                .name("수도권 6호선")
                .stationCode(1)
                .stations(Arrays.asList(myFindRoadStation))
                .build();
        //when

        MyFindRoadSubPath resultMyFindRoadSubPath = myFindRoadSubPath.updateStations(updatedStations);
        //then
        assertThat(resultMyFindRoadSubPath.getStations().size()).isEqualTo(2);
        assertThat(resultMyFindRoadSubPath.getStations().get(0).getStationName()).isEqualTo("삼각지");
        assertThat(resultMyFindRoadSubPath.getStations().get(0).getIndex()).isEqualTo(2);
        assertThat(resultMyFindRoadSubPath.getStations().get(1).getStationName()).isEqualTo("녹사평");
        assertThat(resultMyFindRoadSubPath.getStations().get(1).getIndex()).isEqualTo(3);
    }
}