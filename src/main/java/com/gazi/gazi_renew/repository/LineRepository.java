package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<Line> findByLineName(String lineName);
}
