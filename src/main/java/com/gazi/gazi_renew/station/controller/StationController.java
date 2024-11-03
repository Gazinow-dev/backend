package com.gazi.gazi_renew.station.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import com.gazi.gazi_renew.station.infrastructure.SubwayRepository;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/stations")
@RestController
public class StationController extends BaseController {

    private final SubwayRepository subwayRepository;

    @Hidden
    @GetMapping
    public List<StationEntity> getStationsByLine(@RequestParam("line") String line) {
        return subwayRepository.findByLine(line);
    }
}
