package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindLoadStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFindLoadStationRepository extends JpaRepository<MyFindLoadStation, Long> {
}
