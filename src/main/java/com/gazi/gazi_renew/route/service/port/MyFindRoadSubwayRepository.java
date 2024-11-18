package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.route.domain.MyFindRoadStation;

import java.util.List;

public interface MyFindRoadSubwayRepository {
    void save(MyFindRoadStation myFindRoadStation);

    List<MyFindRoadStation> findAllByMyFindRoadSubPathId(Long myFindRoadSubPathId);

    void deleteAll(List<MyFindRoadStation> myFindRoadStationList);
}
