package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLane;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_lane")
@Entity
public class MyFindRoadLaneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // 노선명
    private int stationCode; //노선코드 ex:) 2
    private String startName; //승차 정류장
    private String endName; // 하차 정류장
    @ManyToOne
    @JoinColumn(name = "my_find_road_sub_path_id", nullable = false)
    private MyFindRoadSubPathEntity myFindRoadSubPathEntity;

    public static MyFindRoadLaneEntity from(MyFindRoadLane myFindRoadLane) {
        MyFindRoadLaneEntity myFindRoadLaneEntity = new MyFindRoadLaneEntity();
        myFindRoadLaneEntity.name = myFindRoadLane.getName();
        myFindRoadLaneEntity.stationCode = myFindRoadLane.getStationCode();
        myFindRoadLaneEntity.startName = myFindRoadLane.getStartName();
        myFindRoadLaneEntity.endName = myFindRoadLane.getEndName();

        return myFindRoadLaneEntity;
    }

    public MyFindRoadLane toModel() {
        return MyFindRoadLane.builder()
                .name(name)
                .stationCode(stationCode)
                .startName(startName)
                .endName(endName)
                .build();
    }
}
