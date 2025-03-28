package com.gazi.gazi_renew.station.infrastructure;

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

    List<StationEntity> findByLine(String line);

    List<StationEntity> findByIssueStationCodeBetween(int lowerCode, int upperCode);
    List<StationEntity> findByNameContainingAndLine(String name, String line);
    @Query(value = "SELECT * " +
            "FROM station " +
            "WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 400)",
            nativeQuery = true)
    List<StationEntity> findNearbyStations(@Param("latitude") double latitude,
                                           @Param("longitude") double longitude);
}