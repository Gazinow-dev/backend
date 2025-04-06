package com.gazi.gazi_renew.common.aspect;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.dto.MemberLogin;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.github.dockerjava.api.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.file.AccessDeniedException;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminLoginAspect {

    private final MemberRepository memberRepository;

    @Around("@annotation(com.gazi.gazi_renew.common.aspect.AdminLoginCheck)")
    public Object checkAdminLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        String referer = request.getHeader("Referer");

        if (referer != null && referer.contains("/admin/login")) {
            // request body에서 email 꺼내기
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof MemberLogin memberLogin) {
                    Member member = memberRepository.findByEmail(memberLogin.getEmail())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원의 아이디입니다."));

                    if (member.getRole().equals(Role.ROLE_USER)) {
                        throw new AccessDeniedException("관리자 권한이 없습니다.");
                    }
                }
            }
        }

        return joinPoint.proceed();
    }
}
