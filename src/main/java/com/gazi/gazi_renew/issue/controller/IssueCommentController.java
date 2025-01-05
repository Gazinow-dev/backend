package com.gazi.gazi_renew.issue.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.controller.port.IssueCommentService;
import com.gazi.gazi_renew.issue.controller.response.IssueCommentResponse;
import com.gazi.gazi_renew.issue.controller.response.MyCommentSummaryResponse;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@SecurityRequirement(name = "Bearer Authentication")
public class IssueCommentController extends BaseController {
    private final IssueCommentService issueCommentService;
    private final ClockHolder clockHolder;
    private final Response response;
    @Operation(summary = "이슈 댓글 작성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 완료"),
            @ApiResponse(responseCode = "400", description = "댓글 내용은 500자를 넘을 수 없습니다.")
    }
    )
    @PostMapping
    public ResponseEntity<Response.Body> saveComment(@Valid @RequestBody IssueCommentCreate issueCommentCreate) {
        IssueComment issueComment = issueCommentService.saveComment(issueCommentCreate);
        return response.success(IssueCommentResponse.from(issueComment, clockHolder), "댓글 작성 완료", HttpStatus.CREATED);
    }
    @Operation(summary = "내가 작성한 댓글 조회 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "내가 작성한 댓글 조회 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = IssueCommentResponse.class)))})
    @GetMapping
    public ResponseEntity<Response.Body> getIssueCommentsByMemberId() {
        List<MyCommentSummary> issueComments = issueCommentService.getIssueCommentsByMemberId();
        return response.success(MyCommentSummaryResponse.fromList(issueComments), "내가 작성한 댓글 조회 완료", HttpStatus.OK);
    }
    @Operation(summary = "이슈에 달린 댓글 조회 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "내가 작성한 댓글 조회 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = IssueCommentResponse.class)))})
    @GetMapping("/{issueId}")
    public ResponseEntity<Response.Body> getIssueCommentsByIssueId(@PathVariable Long issueId) {
        List<IssueComment> issueCommentList = issueCommentService.getIssueCommentByIssueId(issueId);
        return response.success(IssueCommentResponse.fromList(issueCommentList, clockHolder), "이슈에 달린 댓글 조회 완료", HttpStatus.OK);
    }

    @Operation(summary = "댓글 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 수정 완료"),
            @ApiResponse(responseCode = "400", description = "댓글 내용은 500자를 넘을 수 없습니다.")
    })
    @PatchMapping
    public ResponseEntity<Response.Body> updateIssueComment(@Valid @RequestBody IssueCommentUpdate issueCommentUpdate) {
        IssueComment issueComment = issueCommentService.updateIssueComment(issueCommentUpdate);
        return response.success(IssueCommentResponse.from(issueComment, clockHolder), "댓글 수정 완료", HttpStatus.OK);
    }
    @Operation(summary = "댓글 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
    })
    @DeleteMapping("/{issueCommentId}")
    public ResponseEntity<Response.Body> deleteIssueComment(@PathVariable Long issueCommentId) {
        issueCommentService.deleteComment(issueCommentId);
        return response.success("댓글 삭제 완료");
    }
}
