package com.gazi.gazi_renew.common.infrastructure;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class SystemClockHolder implements ClockHolder {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}