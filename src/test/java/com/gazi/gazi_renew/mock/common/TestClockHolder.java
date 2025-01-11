package com.gazi.gazi_renew.mock.common;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
public class TestClockHolder implements ClockHolder {
    private final LocalDateTime fixedTime;

    public TestClockHolder(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }

    @Override
    public LocalDateTime now() {
        return fixedTime;
    }
}
