package com.gazi.gazi_renew.route.infrastructure.jpa;

import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadSubPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadSubPathJpaRepository extends JpaRepository<MyFindRoadSubPathEntity,Long> {
    List<MyFindRoadSubPathEntity> findAllByMyFindRoadPathEntityId(Long myFindRoadPathEntityId);
}
