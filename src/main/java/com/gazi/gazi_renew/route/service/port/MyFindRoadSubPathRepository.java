package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.route.domain.MyFindRoad;

import java.util.List;

public interface MyFindRoadSubPathRepository {
    List<MyFindRoad.SubPath> findAllByMyFindRoadPath(MyFindRoad myFindRoad);

}
