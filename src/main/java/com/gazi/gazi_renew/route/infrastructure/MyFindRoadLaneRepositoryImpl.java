package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadLaneJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadLaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class MyFindRoadLaneRepositoryImpl implements MyFindRoadLaneRepository {
    private final MyFindRoadLaneJpaRepository myFindRoadLaneJpaRepository;
    Optional<MyFindRoad> findByMyFindRoadSubPath(MyFindRoad.SubPath myFindRoadSubPath) {
        return myFindRoadLaneJpaRepository.findByMyFindRoadSubPath(myFindRoadSubPath).map(MyFindRoadLaneEntity::toModel);
    }
}
