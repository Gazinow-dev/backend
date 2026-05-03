package com.gazi.gazi_renew.station.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.station.controller.response.StationResponse;
import com.gazi.gazi_renew.station.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/stations")
@RestController
public class StationController extends BaseController {

    private final StationService stationService;

    @Operation(summary = "노선별 역 목록 조회", description = "노선명을 입력하면 해당 노선의 역 목록을 반환합니다. 환승역은 line 필드에 모든 노선이 포함됩니다. '전체' 입력 시 전체 역을 ㄱㄴㄷ 순으로 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "역 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StationResponse.class))))
    })
    @GetMapping
    public List<StationResponse> getStationsByLine(
            @Parameter(description = "노선명 (예: 수도권 2호선) 또는 '전체'") @RequestParam("line") String line) {
        return stationService.getStationsByLine(line);
    }
}
