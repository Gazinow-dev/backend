package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Subway;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubwayRepository extends JpaRepository<Subway, Long> {
    @Query("select s.name,s.line from Subway s where s.name Like:name%")
    List<Double> findByName(@Param("name")String name);

    List<Subway> findByNameStartingWith(String name);

    @Query("select new com.gazi.gazi_renew.dto.SubwayDataResponse(s.lat, s.lng) from Subway s where s.name = :name and s.line = :line")
    SubwayDataResponse findCoordinateByNameAndLine(@Param("name")String name, @Param("line")String line);
}
