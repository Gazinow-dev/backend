package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.controller.port.IssueCommentService;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.issue.service.port.CommentLikesRepository;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IssueCommentServiceImpl implements IssueCommentService {
    private final IssueCommentRepository issueCommentRepository;
    private final MemberRepository memberRepository;
    private final ClockHolder clockHolder;
    private final SecurityUtilService securityUtilService;
    private final IssueRepository issueRepository;
    private final CommentLikesRepository commentLikesRepository;
    @Override
    public IssueComment saveComment(IssueCommentCreate issueCommentCreate) {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        Issue issue = issueRepository.findById(issueCommentCreate.getIssueId())
                .orElseThrow(() -> new EntityNotFoundException("해당 이슈가 존재하지 않습니다."));
        //Testability 높이기
        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, clockHolder);
        return issueCommentRepository.saveComment(issueComment);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<MyCommentSummary> getIssueCommentsByMemberId(Pageable pageable) {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        //TODO : N+1 확인
        Page<IssueComment> issueComments = issueCommentRepository.getIssueComments(pageable, member.getId());

        return issueComments.map(issueComment -> {
            Long issueId = issueComment.getIssue().getId();
            int cnt = issueCommentRepository.countByIssueId(issueId);
            int likesCount = commentLikesRepository.countByIssueCommentId(issueComment.getIssueCommentId());
            return MyCommentSummary.from(issueComment, cnt, likesCount, clockHolder);
        });
    }
    @Override
    public IssueComment updateIssueComment(IssueCommentUpdate issueCommentUpdate) {
        IssueComment issueComment = issueCommentRepository.findByIssueCommentId(issueCommentUpdate.getIssueCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));
        issueComment = issueComment.update(issueCommentUpdate, clockHolder);
        issueCommentRepository.updateIssueComment(issueComment);
        return issueComment;
    }
    @Override
    public void deleteComment(Long issueCommentId) {
        issueCommentRepository.deleteComment(issueCommentId);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<IssueComment> getIssueCommentByIssueId(Pageable pageable, Long issueId) {
        //댓글 조회(신고 횟수 3개) 미만만 조회
        Page<IssueComment> issueCommentList = issueCommentRepository.getIssueCommentByIssueId(pageable, issueId);
        String currentUserEmail = securityUtilService.getCurrentUserEmail();
        if (currentUserEmail.equals("anonymousUser") || currentUserEmail.isEmpty()){
            return issueCommentList;
        }else{
            Member curMember = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                    .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

            return issueCommentList
                    .map(issueComment -> {
                        boolean isMineStatus = issueComment.getMemberId().equals(curMember.getId());
                        int likesCount = commentLikesRepository.countByIssueCommentId(issueComment.getIssueCommentId());
                        boolean isLikesStatus = commentLikesRepository.existByIssueCommentAndMemberId(issueComment, curMember.getId());
                        return issueComment.fromCommentLikes(isMineStatus, likesCount, isLikesStatus);
                    });
        }
    }
}
