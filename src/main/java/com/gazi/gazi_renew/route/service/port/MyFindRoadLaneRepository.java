package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.route.domain.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;

import java.util.Optional;

public interface MyFindRoadLaneRepository {
    Optional<MyFindRoadLane> findByMyFindRoadSubPath(MyFindRoadSubPath myFindRoadSubPath);


    MyFindRoadLane save(MyFindRoadLane myFindRoadLane);
}
