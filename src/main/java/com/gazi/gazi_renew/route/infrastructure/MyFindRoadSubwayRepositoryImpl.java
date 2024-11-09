package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadSubwayJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class MyFindRoadSubwayRepositoryImpl implements MyFindRoadSubwayRepository {
    private final MyFindRoadSubwayJpaRepository myFindRoadSubwayJpaRepository;

    @Override
    public void save(MyFindRoadStation myFindRoadStation, MyFindRoadSubPath myFindRoadSubPath ) {
        myFindRoadSubwayJpaRepository.save(MyFindRoadStationEntity.from(myFindRoadStation, myFindRoadSubPath));
    }
}
