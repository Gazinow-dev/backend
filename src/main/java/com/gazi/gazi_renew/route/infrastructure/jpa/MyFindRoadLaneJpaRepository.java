package com.gazi.gazi_renew.route.infrastructure.jpa;

import com.gazi.gazi_renew.route.infrastructure.MyFindRoadLaneEntity;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadSubPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyFindRoadLaneJpaRepository extends JpaRepository<MyFindRoadLaneEntity,Long> {
    Optional<MyFindRoadLaneEntity> findByMyFindRoadSubPathEntityId(Long myFindRoadSubPathEntityId);
}
