package com.gazi.gazi_renew.station.service.port;

import com.gazi.gazi_renew.station.domain.Station;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubwayRepository {
    List<Station> findByNameStartingWith(String name);

    Station findCoordinateByNameAndLine(@Param("name")String name, @Param("line")String line);

    List<Station> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    List<Station> findByNameContainingAndLine(String name, String line);

    void save(Station station);
}
