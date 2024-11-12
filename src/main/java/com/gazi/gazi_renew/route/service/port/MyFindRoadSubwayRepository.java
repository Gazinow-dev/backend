package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadStationEntity;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadSubPathEntity;

import java.util.List;

public interface MyFindRoadSubwayRepository {
    void save(MyFindRoadStation myFindRoadStation, MyFindRoadSubPath myFindRoadSubPath);

    List<MyFindRoadStation> findAllByMyFindRoadSubPath(MyFindRoadSubPath myFindRoadSubPath);
}
