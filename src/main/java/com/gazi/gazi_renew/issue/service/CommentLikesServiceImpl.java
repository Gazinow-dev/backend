package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.controller.port.CommentLikesService;
import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.dto.CommentLikesCreate;
import com.gazi.gazi_renew.issue.service.port.CommentLikesRepository;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikesServiceImpl implements CommentLikesService {
    private final CommentLikesRepository commentLikesRepository;
    private final MemberRepository memberRepository;
    private final IssueCommentRepository issueCommentRepository;
    private final SecurityUtilService securityUtilService;
    @Override
    @Transactional
    public CommentLikes addLike(CommentLikesCreate commentLikesCreate) {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        IssueComment issueComment = issueCommentRepository.findByIssueCommentId(commentLikesCreate.getIssueCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));
        if (!commentLikesRepository.existByIssueCommentIdAndMemberId(issueComment.getIssueCommentId(), member.getId())) {
            return commentLikesRepository.save(CommentLikes.from(issueComment, member.getId()));
        } else {
            throw ErrorCode.throwDuplicateCommentLikeException();
        }
    }
    @Override
    @Transactional
    public void removeLike(Long issueCommentId) {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        if (!commentLikesRepository.existByIssueCommentIdAndMemberId(issueCommentId, member.getId())) {
            throw new EntityNotFoundException("해당 댓글 좋아요가 존재하지 않습니다.");
        }
        commentLikesRepository.deleteByIssueCommentIdAndMemberId(issueCommentId, member.getId());
    }
}
