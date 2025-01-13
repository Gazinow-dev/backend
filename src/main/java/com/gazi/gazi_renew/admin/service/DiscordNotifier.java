package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class DiscordNotifier {
    @Value("${discord.report.web-hook-url}")
    private String WEBHOOK_URL;
    @Value("${report.url}")
    private String baseUrl;

    public void sendReportNotification(Report report, Member reporterMember, Member reportedMember, IssueComment issueComment) {
        String adminLink = buildAdminLink(
                report,
                reporterMember,
                reportedMember,
                issueComment
        );

        // Discord 메시지 생성
        String message = createDiscordMessage(report, reporterMember, reportedMember, issueComment, adminLink);

        // Discord 웹훅 전송
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> body = new HashMap<>();
        body.put("content", message);

        restTemplate.postForEntity(WEBHOOK_URL, body, String.class);
    }

    private String buildAdminLink(Report report, Member reporterMember, Member reportedMember, IssueComment issueComment) {
        try {
            String reportedAt = URLEncoder.encode(String.valueOf(report.getReportedAt()), StandardCharsets.UTF_8.toString());
            String reporterNickname = URLEncoder.encode(reporterMember.getNickName(), StandardCharsets.UTF_8.toString());
            String reportReason = URLEncoder.encode(report.getReportReason().getDescription(), StandardCharsets.UTF_8.toString());
            String reasonDescription = report.getReasonDescription() != null ? URLEncoder.encode(report.getReasonDescription(), StandardCharsets.UTF_8.toString()) : "";
            String commentCreatedAt = URLEncoder.encode(String.valueOf(issueComment.getCreatedAt()), StandardCharsets.UTF_8.toString());
            String commentContent = URLEncoder.encode(issueComment.getIssueCommentContent(), StandardCharsets.UTF_8.toString());
            String reportedNickname = URLEncoder.encode(reportedMember.getNickName(), StandardCharsets.UTF_8.toString());
            String reportId = URLEncoder.encode(String.valueOf(report.getReportId()), StandardCharsets.UTF_8.toString());

            return baseUrl + reportId+
                    "?reportedAt=" + reportedAt
                    + "&reporterNickname=" + reporterNickname
                    + "&reportReason=" + reportReason
                    + "&reasonDescription=" + reasonDescription
                    + "&commentCreatedAt=" + commentCreatedAt
                    + "&commentContent=" + commentContent
                    + "&reportedNickname=" + reportedNickname;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error while encoding URL parameters", e);
        }
    }
    private String createDiscordMessage(Report report, Member reporterMember, Member reportedMember, IssueComment issueComment, String adminLink) {
        return """
        ## 📢 **신고 접수 알림**\n
        > 🕒 **신고 접수 일시**  
        > %s
        > 🙋‍♂️ **신고자 닉네임**  
        > %s
        > ⚠️ **신고 대상자 닉네임**  
        > %s
        > 📝 **신고 사유**  
        > %s
        > 🗒️ **신고 설명**  
        > %s
        > 📅 **신고된 댓글 작성 일시**  
        > %s
        > 💬 **신고된 댓글 내용**  
        > %s
        🔗 [**신고 처리 관리자 페이지로 이동**](%s)
        """.formatted(
                report.getReportedAt(),                           // 신고 접수 일시
                reporterMember.getNickName(),                     // 신고자 닉네임
                reportedMember.getNickName(),                     // 신고 대상자 닉네임
                report.getReportReason().getDescription(),        // 신고 사유
                report.getReasonDescription(),                    // 신고 설명
                issueComment.getCreatedAt(),                      // 신고된 댓글 작성 일시
                issueComment.getIssueCommentContent(),            // 신고된 댓글 내용
                adminLink                                         // 관리자 페이지 링크
        );
    }


}