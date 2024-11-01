package com.gazi.gazi_renew.route.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadSubwayRepository extends JpaRepository<MyFindRoadStation,Long> {
    List<MyFindRoadStation> findAllByMyFindRoadSubPath(MyFindRoadSubPath subPath);
}
