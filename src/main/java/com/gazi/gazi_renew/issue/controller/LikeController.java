package com.gazi.gazi_renew.issue.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.issue.domain.Like;
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
    private final Response response;
    @PostMapping
    public ResponseEntity<Response.Body> likeIssue(Like like){
        Long id = likeService.likeIssue(like);
        return response.success(id +"번의 좋아요를 눌렀습니다.");
    }
    @DeleteMapping
    public ResponseEntity<Response.Body> deleteLikeIssue(Like like){
        likeService.deleteLikeIssue(like);
        return response.success("데이터 삭제 성공");
    }
}
