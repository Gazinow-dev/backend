package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
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
    public MyFindRoadSubPath save(MyFindRoadSubPath myFindRoadSubPath) {
        return myFindRoadSubPathJpaRepository.save(MyFindRoadSubPathEntity.from(myFindRoadSubPath)).toModel();
    }

}
