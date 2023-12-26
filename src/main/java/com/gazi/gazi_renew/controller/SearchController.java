package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.SubwayDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/search")
@RestController
public class SearchController {
    private final SubwayDataService subwayDataService;

    @GetMapping("/subway")
    public ResponseEntity<Response.Body> SubwayInfos(@RequestParam String subwayName){
        return subwayDataService.getSubwayInfo(subwayName);
    }
}
