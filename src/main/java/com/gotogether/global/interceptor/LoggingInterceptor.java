package com.gotogether.global.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(START_TIME, System.currentTimeMillis());

        logger.info("===================== [로깅 인터셉터 시작] =====================");
        logger.info("[요청] {} {}", request.getMethod(), request.getRequestURI());
        logRequestHeaders(request);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME);
        long duration = (startTime != null) ? (System.currentTimeMillis() - startTime) : -1;

        logRequestBody(request);

        logger.info("-----------------------------------------------------------");
        logger.info("[응답] {} {} (상태: {}, 시간: {}ms)",
            request.getMethod(), request.getRequestURI(), response.getStatus(), duration);

        logResponseHeaders(response);
        logResponseBody(response);
        logger.info("===========================================================\n");
    }

    private void logRequestHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(
            name -> sb.append(name).append("=").append(request.getHeader(name)).append("; ")
        );
        if (sb.length() > 0) {
            logger.info("요청 헤더: {}", sb.toString());
        }
    }

    private void logRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            String body = new String(wrapper.getContentAsByteArray());
            if (!body.isEmpty()) {
                logger.info("요청 바디: {}", body);
            }
        }
    }

    private void logResponseHeaders(HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        response.getHeaderNames().forEach(
            name -> sb.append(name).append("=").append(response.getHeader(name)).append("; ")
        );
        if (sb.length() > 0) {
            logger.info("응답 헤더: {}", sb.toString());
        }
    }

    private void logResponseBody(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            String body = new String(wrapper.getContentAsByteArray());
            if (!body.isEmpty()) {
                logger.info("응답 바디: {}", body);
            }
        }
    }
}
