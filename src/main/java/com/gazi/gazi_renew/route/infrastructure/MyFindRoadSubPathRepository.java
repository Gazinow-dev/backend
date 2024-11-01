package com.gazi.gazi_renew.route.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadSubPathRepository extends JpaRepository<MyFindRoadSubPath,Long> {
    List<MyFindRoadSubPath> findAllByMyFindRoadPath(MyFindRoadPath myFindRoadPath);
}
