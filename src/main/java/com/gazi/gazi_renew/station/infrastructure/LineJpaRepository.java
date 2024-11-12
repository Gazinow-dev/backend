package com.gazi.gazi_renew.station.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineJpaRepository extends JpaRepository<LineEntity, Long> {
    Optional<LineEntity> findByLineName(String lineName);
}
