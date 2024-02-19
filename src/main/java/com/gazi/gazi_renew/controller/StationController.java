package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.repository.SubwayRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/stations")
@RestController
public class StationController extends BaseController{

    private final SubwayRepository subwayRepository;

    @GetMapping
    public List<Station> getStationsByLine(@RequestParam("line") String line) {
        return subwayRepository.findByLine(line);
    }
}
