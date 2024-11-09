package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;

import java.util.List;

public interface MyFindRoadSubwayRepository {
    void save(MyFindRoadStation myFindRoadStation, MyFindRoadSubPath myFindRoadSubPath);
}
