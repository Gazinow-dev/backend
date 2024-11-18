package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "my_find_road_station")
@Entity
public class MyFindRoadStationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int index; // 정류장 순번
    private String stationName;
    private Long myFindRoadSubPathId;
    public static MyFindRoadStationEntity from(MyFindRoadStation myFindRoadStation) {
        MyFindRoadStationEntity myFindRoadStationEntity = new MyFindRoadStationEntity();
        myFindRoadStationEntity.index= myFindRoadStation.getIndex();
        myFindRoadStationEntity.stationName = myFindRoadStation.getStationName();
        myFindRoadStationEntity.myFindRoadSubPathId = myFindRoadStation.getMyFindRoadSubPathId();

        return myFindRoadStationEntity;
    }

    public MyFindRoadStation toModel() {
        return MyFindRoadStation.builder()
                .id(id)
                .index(index)
                .stationName(stationName)
                .myFindRoadSubPathId(myFindRoadSubPathId)
                .build();
    }
}
