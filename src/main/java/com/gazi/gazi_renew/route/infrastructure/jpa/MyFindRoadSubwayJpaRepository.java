package com.gazi.gazi_renew.route.infrastructure.jpa;

import com.gazi.gazi_renew.route.infrastructure.MyFindRoadStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadSubwayJpaRepository extends JpaRepository<MyFindRoadStationEntity,Long> {
    List<MyFindRoadStationEntity> findAllByMyFindRoadSubPathId(Long myFindRoadSubPathId);
}
