package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.member.domain.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class DiscordNotifier {
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/{your_webhook_id}/{your_webhook_token}";

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
            String baseUrl = "https://admin.example.com/reports/";

            String reportedAt = URLEncoder.encode(String.valueOf(report.getReportedAt()), StandardCharsets.UTF_8.toString());
            String reporterNickname = URLEncoder.encode(reporterMember.getNickName(), StandardCharsets.UTF_8.toString());
            String reportReason = URLEncoder.encode(String.valueOf(report.getReportReason()), StandardCharsets.UTF_8.toString());
            String reasonDescription = URLEncoder.encode(report.getReasonDescription(), StandardCharsets.UTF_8.toString());
            String commentCreatedAt = URLEncoder.encode(String.valueOf(issueComment.getCreatedAt()), StandardCharsets.UTF_8.toString());
            String commentContent = URLEncoder.encode(issueComment.getIssueCommentContent(), StandardCharsets.UTF_8.toString());
            String reportedNickname = URLEncoder.encode(reportedMember.getNickName(), StandardCharsets.UTF_8.toString());

            return baseUrl + "?reportedAt=" + reportedAt
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
        return "📢 **신고 접수 알림**\n\n"
                + "> **🕒 신고 접수 일시**  \n> " + report.getReportedAt() + "  \n\n"
                + "> **🙋‍♂️ 신고자 닉네임**  \n> " + reporterMember.getNickName() + "  \n\n"
                + "> **### 신고 이유**  \n> " + report.getReportReason().getDescription() + "  \n\n"
                + "> **- 신고 설명**  \n> " + report.getReasonDescription() + "  \n\n"
                + "> **📅 신고된 댓글 작성 일시**  \n> " + issueComment.getCreatedAt() + "  \n\n"
                + "> **💬 신고된 댓글 내용**  \n> \"" + issueComment.getIssueCommentContent() + "\"  \n\n"
                + "> **⚠️ 신고 대상자**  \n> " + reportedMember.getNickName() + "  \n\n"
                + "🔗 [신고 처리 관리자 페이지로 이동](" + adminLink + ")";
    }
}