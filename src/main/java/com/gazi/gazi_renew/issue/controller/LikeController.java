package com.gazi.gazi_renew.issue.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.issue.domain.LikeRequest;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.port.LikeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/v1/like")
public class LikeController extends BaseController {

    private final LikeService likeService;
    @PostMapping
    public ResponseEntity<Response.Body> likeIssue(LikeRequest dto){
        return likeService.likeIssue(dto);
    }
    @DeleteMapping
    public ResponseEntity<Response.Body> deleteLikeIssue(LikeRequest dto){
        return likeService.deleteLikeIssue(dto);
    }
}
