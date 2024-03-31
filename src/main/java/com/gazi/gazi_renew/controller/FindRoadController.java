package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.FindRoadRequest;
import com.gazi.gazi_renew.dto.FindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.FindRoadService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/find_road")
@RestController
public class FindRoadController extends BaseController{
    private final FindRoadService findRoadService;

    @Hidden
    @GetMapping("/subway")
    public ResponseEntity<Response.Body> subwayRouteSearch(@RequestParam Long CID, @RequestParam Long SID, @RequestParam Long EID, @RequestParam int sopt) throws IOException {
        return findRoadService.subwayRouteSearch(CID, SID, EID, sopt);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "길찾기 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "길찾기 데이터 조회 성공",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FindRoadResponse.class)))})
    @GetMapping
    public ResponseEntity<Response.Body> findSubwayList(
            @RequestParam String strStationName,
            @RequestParam String strStationLine,
            @RequestParam String endStationName,
            @RequestParam String endStationLine
    ) throws IOException {

        FindRoadRequest request = FindRoadRequest.builder()
                .strStationName(strStationName)
                .strStationLine(strStationLine)
                .endStationName(endStationName)
                .endStationLine(endStationLine).build();

        return findRoadService.findRoad(request);
    }

}
