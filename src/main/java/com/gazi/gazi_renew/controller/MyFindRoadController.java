package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.MyFindRoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/my_find_road")
@RestController
public class MyFindRoadController {
    private final MyFindRoadService myFindRoadService;

    @GetMapping("/get_roads")
    public ResponseEntity<Response.Body> getRoutes() {
        return myFindRoadService.getRoutes();
    }

    @Operation(summary = "내 경로 저장")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "내 경로 저장 성공",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"
                    )
            ),
            @ApiResponse(
                    responseCode = "500", description = "회원이 존재하지 않습니다."
            )
    })
    @PostMapping("/add_route")
    public ResponseEntity<Response.Body> addRoute(@RequestBody MyFindRoadRequest request) {
        return myFindRoadService.addRoute(request);
    }

    @Operation(summary = "내 경로 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "내 경로 삭제 성공",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "회원이 존재하지 않습니다."
            ),
            @ApiResponse(
                    responseCode = "400", description = "해당 id로 존재하는 MyFindRoad가 없습니다."
            )
    })
    @DeleteMapping("/delete_route")
    public ResponseEntity<Response.Body> deleteRoute(@RequestParam Long id) {
        return myFindRoadService.deleteRoute(id);
    }
}
