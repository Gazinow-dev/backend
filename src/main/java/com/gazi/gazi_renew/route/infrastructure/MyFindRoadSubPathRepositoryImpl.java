package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadSubPathJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyFindRoadSubPathRepositoryImpl implements MyFindRoadSubPathRepository {
    private final MyFindRoadSubPathJpaRepository myFindRoadSubPathJpaRepository;
    @Override
    public MyFindRoadSubPath save(MyFindRoadSubPath myFindRoadSubPath) {
        MyFindRoadSubPath result = myFindRoadSubPathJpaRepository.save(MyFindRoadSubPathEntity.from(myFindRoadSubPath)).toModel();
        return result;

    }
    @Override
    public List<MyFindRoadSubPath> findByMyFindRoadPathId(Long myFindRoadPathId) {
        return Optional.ofNullable(myFindRoadSubPathJpaRepository.findAllByMyFindRoadPathEntityId(myFindRoadPathId))
                .orElse(Collections.emptyList())
                .stream()
                .map(MyFindRoadSubPathEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll(List<MyFindRoadSubPath> myFindRoadSubPathList) {
        myFindRoadSubPathJpaRepository.deleteAll(myFindRoadSubPathList.stream()
                .map(MyFindRoadSubPathEntity::from)
                .collect(Collectors.toList()));
    }

}
