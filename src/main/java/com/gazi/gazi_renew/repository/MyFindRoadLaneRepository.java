package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindRoadLane;
import com.gazi.gazi_renew.domain.MyFindRoadSubPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyFindRoadLaneRepository extends JpaRepository<MyFindRoadLane,Long> {
    Optional<MyFindRoadLane> findByMyFindRoadSubPath(MyFindRoadSubPath myFindRoadSubPath);
}
