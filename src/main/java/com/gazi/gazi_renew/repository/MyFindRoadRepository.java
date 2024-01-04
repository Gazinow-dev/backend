package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindRoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFindRoadRepository extends JpaRepository<MyFindRoad, Long> {
}
