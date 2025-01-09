package com.gazi.gazi_renew.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.admin.service.port.MemberPenaltyRepository;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.controller.response.Response.Body;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CommentRestrictionFilter extends OncePerRequestFilter {

    private final MemberPenaltyRepository memberPenaltyRepository;
    private final MemberRepository memberRepository;
    private final Response responseBuilder;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 댓글 작성 API가 아닌 경우 필터를 건너뜀
        if (!request.getRequestURI().startsWith("/api/v1/comments")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 현재 인증된 사용자 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 사용자 ID 조회
            Member member = memberRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
            Long memberId = member.getId();

            // 제재 상태 확인
            boolean isRestricted = memberPenaltyRepository.isMemberRestricted(memberId);

            if (isRestricted) {
                // 제재된 사용자에 대해 403 응답 반환
                Body body = responseBuilder.fail(
                                "댓글 작성이 제한된 사용자입니다.",
                                HttpStatus.FORBIDDEN
                        )
                        .getBody();

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter()
                        .write(objectMapper.writeValueAsString(body));
                return;
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
