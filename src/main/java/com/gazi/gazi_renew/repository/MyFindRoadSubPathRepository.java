package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.MyFindRoadSubPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadSubPathRepository extends JpaRepository<MyFindRoadSubPath,Long> {
    List<MyFindRoadSubPath> findAllByMyFindRoadPath(MyFindRoadPath myFindRoadPath);
}
