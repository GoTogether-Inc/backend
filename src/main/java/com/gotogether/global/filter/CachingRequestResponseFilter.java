package com.gotogether.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

// TODO: 요청 바디 로깅 필터 추가
@Component
public class CachingRequestResponseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(httpResponse);
        
        chain.doFilter(httpRequest, cachingResponse);
        cachingResponse.copyBodyToResponse();
    }
}
