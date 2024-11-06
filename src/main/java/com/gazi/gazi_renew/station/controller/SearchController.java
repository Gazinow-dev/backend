package com.gazi.gazi_renew.station.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.station.controller.response.SubwayInfoResponse;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
@RestController
public class SearchController extends BaseController {
    private final StationService stationService;
    private final Response response;

    @Operation(summary = "지하철 검색")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "지하철 검색 성공",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SubwayInfoResponse.class)))})
    @GetMapping("/station")
    public ResponseEntity<Response.Body> SubwayInfos(@Parameter(description = "지하철 이름") @RequestParam String stationName) {
        List<Station> subwayInfo = stationService.getSubwayInfo(stationName);
        return response.success(SubwayInfoResponse.fromList(subwayInfo), "지하철 검색 성공", HttpStatus.OK);
    }
}
