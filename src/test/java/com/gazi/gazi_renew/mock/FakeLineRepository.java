package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.service.port.LineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeLineRepository implements LineRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Line> data = new ArrayList<>();

    @Override
    public Optional<Line> findByLineName(String lineName) {
        return data.stream()
                .filter(line -> line.getLineName().equals(lineName))
                .findFirst();
    }

    @Override
    public Line save(Line line) {
        if (line.getId() == null || line.getId() == 0) {
            Line createLine = Line.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .lineName(line.getLineName())
                    .build();
            data.add(createLine);
            return createLine;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), line.getId()));
            data.add(line);
            return line;
        }
    }

    @Override
    public Optional<Line> findById(Long id) {
        return data.stream()
                .filter(line -> line.getId().equals(id)).findFirst();
    }
}