package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.controller.port.IssueCommentService;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentDelete;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueCommentServiceImpl implements IssueCommentService {
    private final IssueCommentRepository issueCommentRepository;
    private final MemberRepository memberRepository;
    private final ClockHolder clockHolder;
    private final SecurityUtilService securityUtilService;
    @Override
    public IssueComment saveComment(IssueCommentCreate issueCommentCreate) {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        //Testability 높이기
        IssueComment issueComment = IssueComment.from(issueCommentCreate, member, clockHolder);
        return issueCommentRepository.saveComment(issueComment);
    }

    @Override
    public List<IssueComment> getIssueComments() {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        return issueCommentRepository.getIssueComments(member.getId());
    }

    @Override
    public IssueComment updateIssueComment(IssueCommentUpdate issueCommentUpdate) {
        IssueComment issueComment = issueCommentRepository.findByIssueCommentId(issueCommentUpdate.getIssueCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));
        issueComment = issueComment.update(issueCommentUpdate, clockHolder);

        return issueCommentRepository.updateIssueComment(issueComment);
    }

    @Override
    public void deleteComment(IssueCommentDelete issueCommentDelete) {
        issueCommentRepository.deleteComment(issueCommentDelete.getIssueCommentId());
    }
}
