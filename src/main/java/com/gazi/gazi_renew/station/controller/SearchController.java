package com.gazi.gazi_renew.station.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.station.controller.response.SubwayDataResponse;
import com.gazi.gazi_renew.station.service.SubwayDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
@RestController
public class SearchController extends BaseController {
    private final SubwayDataService subwayDataService;

    @Operation(summary = "지하철 검색")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "지하철 검색 성공",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SubwayDataResponse.SubwayInfo.class)))})
    @GetMapping("/station")
    public ResponseEntity<Response.Body> SubwayInfos(@Parameter(description = "지하철 이름") @RequestParam String stationName) {
        return subwayDataService.getSubwayInfo(stationName);
    }
}
