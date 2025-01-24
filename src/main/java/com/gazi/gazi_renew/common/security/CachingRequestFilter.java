package com.gazi.gazi_renew.common.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class CachingRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {

            ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper( (HttpServletRequest) request);
            ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

            chain.doFilter(wrappingRequest, wrappingResponse);

            wrappingResponse.copyBodyToResponse();
        } else {
            chain.doFilter(request, response);
        }
    }
}
