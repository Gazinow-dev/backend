package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadLaneJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadLaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class MyFindRoadLaneRepositoryImpl implements MyFindRoadLaneRepository {
    private final MyFindRoadLaneJpaRepository myFindRoadLaneJpaRepository;
    public Optional<MyFindRoadLane> findByMyFindRoadSubPath(MyFindRoadSubPath myFindRoadSubPath) {
        return myFindRoadLaneJpaRepository.findByMyFindRoadSubPath(MyFindRoadSubPathEntity.from(myFindRoadSubPath)).map(MyFindRoadLaneEntity::toModel);
    }

    @Override
    public MyFindRoadLane save(MyFindRoadLane myFindRoadLane) {
        return myFindRoadLaneJpaRepository.save(MyFindRoadLaneEntity.from(myFindRoadLane)).toModel();
    }

}
