package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    List<Station> findByStationCodeBetween(int lowerCode, int upperCode);
//    @Query("SELECT s.issues FROM Station s JOIN FETCH s.issues WHERE s.stationCode = :stationCode")
//    List<Issue> findWithIssuesByStationCode(@Param("stationCode") int stationCode);

}