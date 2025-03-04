package com.gazi.gazi_renew.member.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.controller.response.RecentSearchResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.member.controller.port.RecentSearchService;
import com.gazi.gazi_renew.member.domain.dto.RecentSearchCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/recentSearch")
@RestController
public class RecentSearchController extends BaseController {

    private final RecentSearchService recentSearchService;
    private final Response response;
    // 최근검색조회
    @Operation(summary = "최근검색 조회")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "최근검색 조회",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)))})
    @GetMapping
    public ResponseEntity<Response.Body> getRecentSearchList() {
        List<RecentSearch> recentSearchList = recentSearchService.getRecentSearch();
        return response.success(RecentSearchResponse.fromList(recentSearchList));
    }

    @Operation(summary = "최근검색 저장")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "최근검색 저장완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)))})
    @PostMapping("/add")
    public ResponseEntity<Response.Body> signup(@RequestBody RecentSearchCreate recentSearchCreate) {
        RecentSearch recentSearch = recentSearchService.addRecentSearch(recentSearchCreate);
        return response.success(RecentSearchResponse.from(recentSearch),"최근검색 추가 성공", HttpStatus.CREATED);
    }

    @Operation(summary = "최근검색 삭제")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "최근검색 삭제완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)))})
    @DeleteMapping("/delete")
    public ResponseEntity<Response.Body> delete(@Parameter(description = "최근검색ID") @RequestParam Long recentId) {
        recentSearchService.recentDelete(recentId);
        return response.success("검색결과가 삭제되었습니다.");
    }
}
