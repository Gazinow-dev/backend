package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubwayRepository extends JpaRepository<Station, Long> {
    @Query("select s.name,s.line from Station s where s.name Like:name%")
    List<Double> findByName(@Param("name")String name);

    List<Station> findByNameStartingWith(String name);

    @Query("select new com.gazi.gazi_renew.dto.SubwayDataResponse(s.lat, s.lng) from Station s where s.name = :name and s.line = :line")
    SubwayDataResponse findCoordinateByNameAndLine(@Param("name")String name, @Param("line")String line);

    boolean existsByCode(int code);
}
