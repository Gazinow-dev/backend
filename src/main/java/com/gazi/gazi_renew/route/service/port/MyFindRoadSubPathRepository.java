package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;

import java.util.List;

public interface MyFindRoadSubPathRepository {
    MyFindRoadSubPath save(MyFindRoadSubPath myFindRoadSubPath);

    List<MyFindRoadSubPath> findByMyFindRoadPathId(Long myFindRoadPathId);

    void deleteAll(List<MyFindRoadSubPath> myFindRoadSubPathList);
}
