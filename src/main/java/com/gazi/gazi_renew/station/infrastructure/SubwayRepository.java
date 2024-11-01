package com.gazi.gazi_renew.station.infrastructure;

import com.gazi.gazi_renew.station.infrastructure.Station;
import com.gazi.gazi_renew.station.controller.response.SubwayDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubwayRepository extends JpaRepository<Station, Long> {
    @Query("select s.name,s.line from Station s where s.name Like:name%")
    List<Double> findByName(@Param("name")String name);
    List<Station> findByNameStartingWith(String name);
    @Query("select new com.gazi.gazi_renew.dto.SubwayDataResponse(s.lat, s.lng) from Station s where s.name = :name and s.line = :line")
    SubwayDataResponse findCoordinateByNameAndLine(@Param("name")String name, @Param("line")String line);
    boolean existsByStationCode(int stationCode);

    List<Station> findByLine(String line);

    List<Station> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    @Transactional(readOnly = true)
    List<Station> findByNameContainingAndLine(String name, String line);

    Station findByNameAndLine(String stationName, String line);
}