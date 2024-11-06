package com.gazi.gazi_renew.station.infrastructure;

import com.gazi.gazi_renew.station.controller.response.SubwayDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubwayJpaRepository extends JpaRepository<StationEntity, Long> {
    List<StationEntity> findByNameStartingWith(String name);

    StationEntity findByNameAndLine(String name, String line);

    boolean existsByStationCode(int stationCode);

    List<StationEntity> findByLine(String line);

    List<StationEntity> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    @Transactional(readOnly = true)
    List<StationEntity> findByNameContainingAndLine(String name, String line);
}