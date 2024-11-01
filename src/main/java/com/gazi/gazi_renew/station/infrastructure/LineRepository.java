package com.gazi.gazi_renew.station.infrastructure;

import com.gazi.gazi_renew.station.infrastructure.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<Line> findByLineName(String lineName);
}
