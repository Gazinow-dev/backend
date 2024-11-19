package com.gazi.gazi_renew.station.infrastructure;

import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.service.port.LineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LineRepositoryImpl implements LineRepository {
    private final LineJpaRepository lineJpaRepository;

    @Override
    public Optional<Line> findByLineName(String lineName) {
        return lineJpaRepository.findByLineName(lineName).map(LineEntity::toModel);
    }

    @Override
    public Line save(Line line) {
        return lineJpaRepository.save(LineEntity.from(line)).toModel();
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lineJpaRepository.findById(id).map(LineEntity::toModel);
    }
}
