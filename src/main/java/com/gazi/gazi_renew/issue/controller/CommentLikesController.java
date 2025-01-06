package com.gazi.gazi_renew.issue.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.port.CommentLikesService;
import com.gazi.gazi_renew.issue.controller.response.CommentLikesResponse;
import com.gazi.gazi_renew.issue.controller.response.IssueCommentResponse;
import com.gazi.gazi_renew.issue.controller.response.MyCommentSummaryResponse;
import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.dto.CommentLikesCreate;
import io.swagger.v3.oas.annotations.Operation;
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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments/likes")
@SecurityRequirement(name = "Bearer Authentication")
public class CommentLikesController extends BaseController {
    private final CommentLikesService commentLikesService;
    private final Response response;

    @Operation(summary = "댓글 좋아요 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "댓글 좋아요 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentLikesResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 좋아요를 누른 댓글입니다.")})
    @PostMapping
    public ResponseEntity<Response.Body> addCommentLike(@RequestBody CommentLikesCreate commentLikesCreate) {
        CommentLikes commentLikes = commentLikesService.addLike(commentLikesCreate);
        return response.success(CommentLikesResponse.from(commentLikes), "댓글 좋아요 완료", HttpStatus.CREATED);
    }
    @Operation(summary = "댓글 좋아요 취소 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "댓글 좋아요 취소 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"))})
    @DeleteMapping("/{commentLikesId}")
    public void removeCommentLike(@PathVariable Long commentLikesId) {
        commentLikesService.removeLike(commentLikesId);
    }

}
