package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindRoadStation;
import com.gazi.gazi_renew.domain.MyFindRoadSubPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadSubwayRepository extends JpaRepository<MyFindRoadStation,Long> {
    List<MyFindRoadStation> findAllByMyFindRoadSubPath(MyFindRoadSubPath subPath);
}
