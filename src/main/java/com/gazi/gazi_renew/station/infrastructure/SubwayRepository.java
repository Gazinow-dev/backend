package com.gazi.gazi_renew.station.infrastructure;

import com.gazi.gazi_renew.station.controller.response.SubwayDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubwayRepository extends JpaRepository<StationEntity, Long> {
    @Query("select s.name,s.line from StationEntity s where s.name Like:name%")
    List<Double> findByName(@Param("name")String name);
    List<StationEntity> findByNameStartingWith(String name);
    @Query("select new com.gazi.gazi_renew.dto.SubwayDataResponse(s.lat, s.lng) from StationEntity s where s.name = :name and s.line = :line")
    SubwayDataResponse findCoordinateByNameAndLine(@Param("name")String name, @Param("line")String line);
    boolean existsByStationCode(int stationCode);

    List<StationEntity> findByLine(String line);

    List<StationEntity> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    @Transactional(readOnly = true)
    List<StationEntity> findByNameContainingAndLine(String name, String line);

    StationEntity findByNameAndLine(String stationName, String line);
}