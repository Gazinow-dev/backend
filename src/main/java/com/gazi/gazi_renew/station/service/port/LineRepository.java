package com.gazi.gazi_renew.station.service.port;

import com.gazi.gazi_renew.station.domain.Line;
import java.util.Optional;

public interface LineRepository {
    Optional<Line> findByLineName(String lineName);

    Line save(Line line);

    Optional<Line> findById(Long id);
}
