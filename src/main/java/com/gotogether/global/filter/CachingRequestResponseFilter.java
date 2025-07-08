package com.gotogether.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class CachingRequestResponseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        chain.doFilter(cachingRequest, cachingResponse);

        cachingResponse.copyBodyToResponse();
    }
}
