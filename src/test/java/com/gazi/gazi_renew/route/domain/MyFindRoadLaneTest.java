package com.gazi.gazi_renew.route.domain;


import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLaneCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MyFindRoadLaneTest {
    @Test
    void MyFindRoadLane은_MyFindRoadLaneCreate_객체로_생성할_수_있다() throws Exception{
        //given
        MyFindRoadLaneCreate myFindRoadLaneCreate = MyFindRoadLaneCreate.builder()
                .name("수도권 6호선")
                .stationCode(2)
                .build();
        //when
        MyFindRoadLane myFindRoadLane = MyFindRoadLane.from(myFindRoadLaneCreate);
        //then
        Assertions.assertThat(myFindRoadLane.getName()).isEqualTo("수도권 6호선");
        Assertions.assertThat(myFindRoadLane.getStationCode()).isEqualTo(2);
    }
}