package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
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
    @ManyToOne
    @JoinColumn(name = "my_find_road_sub_path_id", nullable = false)
    private MyFindRoadSubPathEntity myFindRoadSubPathEntity;
    public static MyFindRoadStationEntity from(MyFindRoadStation myFindRoadStation, MyFindRoadSubPath myFindRoadSubPath) {
        MyFindRoadStationEntity myFindRoadStationEntity = new MyFindRoadStationEntity();
        myFindRoadStationEntity.index= myFindRoadStation.getIndex();
        myFindRoadStationEntity.stationName = myFindRoadStation.getStationName();
        myFindRoadStationEntity.myFindRoadSubPathEntity = MyFindRoadSubPathEntity.from(myFindRoadSubPath);

        return null;
    }
}
