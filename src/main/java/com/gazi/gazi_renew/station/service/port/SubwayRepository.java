package com.gazi.gazi_renew.station.service.port;

import com.gazi.gazi_renew.station.controller.response.SubwayDataResponse;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubwayRepository {
    List<Station> findByNameStartingWith(String name);

    Station findCoordinateByNameAndLine(@Param("name")String name, @Param("line")String line);
    boolean existsByStationCode(int stationCode);

    List<Station> findByLine(String line);

    List<Station> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    List<Station> findByNameContainingAndLine(String name, String line);
}
