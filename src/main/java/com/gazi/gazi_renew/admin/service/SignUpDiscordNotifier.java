package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class SignUpDiscordNotifier {
    @Value("${discord.sign-up.web-hook-url}")
    private String SIGNUP_WEBHOOK_URL;

    public void sendSignUpNotification(Member member, int count) {
        // Discord 메시지 생성
        String message = createDiscordMessage(member, count);

        // Discord 웹훅 전송
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> body = new HashMap<>();
        body.put("content", message);

        restTemplate.postForEntity(SIGNUP_WEBHOOK_URL, body, String.class);
    }
    private String createDiscordMessage(Member member, int count) {
        return """
        ## 📢 **회원가입 알림**\n
        🚀 [회원 가입] %d번째 유저가 가입하였습니다. 🚀
        가는길 지금에 새로운 유저가 가입하였습니다.

        **[이름]**  
        %s  
        **[이메일]**  
        %s  
        **[소셜 플랫폼]**  
        %s  
        **[가입 일시]**  
        %s  
        """.formatted(
                count,                           // 가입 순번
                member.getNickName(),            // 회원 닉네임
                member.getEmail(),               // 회원 이메일
                member.getProvider() != null ? member.getProvider() : "자체 회원가입",      // 가입한 소셜 플랫폼
                LocalDateTime.now()            // 가입 일시
        );
    }
}
