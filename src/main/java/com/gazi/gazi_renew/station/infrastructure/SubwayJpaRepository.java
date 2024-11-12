package com.gazi.gazi_renew.station.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubwayJpaRepository extends JpaRepository<StationEntity, Long> {
    List<StationEntity> findByNameStartingWith(String name);

    StationEntity findByNameAndLine(String name, String line);

    List<StationEntity> findByLine(String line);

    List<StationEntity> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    @Transactional(readOnly = true)
    List<StationEntity> findByNameContainingAndLine(String name, String line);
}