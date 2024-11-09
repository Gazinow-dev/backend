package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadSubPathJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyFindRoadSubPathRepositoryImpl implements MyFindRoadSubPathRepository {
    private final MyFindRoadSubPathJpaRepository myFindRoadSubPathJpaRepository;

    @Override
    public List<MyFindRoad.SubPath> findAllByMyFindRoadPath(MyFindRoad myFindRoad) {
        return myFindRoadSubPathJpaRepository.findAllByMyFindRoadPath(MyFindRoadPathEntity.from(myFindRoad)).stream()
                .map(MyFindRoadSubPathEntity::toModel)
                .collect(Collectors.toList());
    }
}
