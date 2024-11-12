package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadSubwayJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class MyFindRoadSubwayRepositoryImpl implements MyFindRoadSubwayRepository {
    private final MyFindRoadSubwayJpaRepository myFindRoadSubwayJpaRepository;

    @Override
    public void save(MyFindRoadStation myFindRoadStation, MyFindRoadSubPath myFindRoadSubPath ) {
        myFindRoadSubwayJpaRepository.save(MyFindRoadStationEntity.from(myFindRoadStation, myFindRoadSubPath));
    }

    @Override
    public List<MyFindRoadStation> findAllByMyFindRoadSubPath(MyFindRoadSubPath myFindRoadSubPath) {
        return myFindRoadSubwayJpaRepository.findAllByMyFindRoadSubPathEntityId(myFindRoadSubPath.getId())
                .stream().map(MyFindRoadStationEntity::toModel).collect(Collectors.toList());
    }
}
