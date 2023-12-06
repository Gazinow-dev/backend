package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.FindRoadRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.FindRoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/find_road")
@RestController
public class FindRoadController {
    private final FindRoadService findRoadService;

    @GetMapping("/subway")
    public ResponseEntity<Response.Body> subwayRouteSearch(@RequestParam Long  CID,@RequestParam Long SID, @RequestParam Long EID,@RequestParam int sopt) throws IOException{
        return findRoadService.subwayRouteSearch(CID, SID, EID, sopt);
    }

    @GetMapping
    public ResponseEntity<Response.Body> findSubwayList(@RequestBody FindRoadRequest request) throws IOException {
        return findRoadService.findRoad(request);
    }

}
