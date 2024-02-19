package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.RecentSearchRequest;
import com.gazi.gazi_renew.dto.RecentSearchResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.RecentSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/recentSearch")
@RestController
public class RecentSearchController extends BaseController{

    private final RecentSearchService recentSearchService;

    // 최근검색조회
    @Operation(summary = "최근검색 조회")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "최근검색 조회",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)))})
    @GetMapping
    public ResponseEntity<Response.Body> getRecentSearchList() {
        return recentSearchService.recentGet();
    }

    @Operation(summary = "최근검색 저장")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "최근검색 저장완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)))})
    @PostMapping("/add")
    public ResponseEntity<Response.Body> signup(@RequestBody RecentSearchRequest dto) {
        return recentSearchService.recentAdd(dto);
    }

    @Operation(summary = "최근검색 삭제")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "최근검색 삭제완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)))})
    @DeleteMapping("/delete")
    public ResponseEntity<Response.Body> delete(@Parameter(description = "최근검색ID") @RequestParam Long recentId) {
        return recentSearchService.recentDelete(recentId);
    }
}
