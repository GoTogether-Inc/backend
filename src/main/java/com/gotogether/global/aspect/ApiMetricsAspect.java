package com.gotogether.global.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object measureApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            
            recordMetrics(joinPoint, System.currentTimeMillis() - startTime, "success");
            
            return result;
            
        } catch (Exception e) {
            recordMetrics(joinPoint, System.currentTimeMillis() - startTime, "error");
            throw e;
        }
    }

    private void recordMetrics(ProceedingJoinPoint joinPoint, long duration, String status) {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) return;

            String method = request.getMethod();
            String path = getNormalizedPath(request.getRequestURI());
            int statusCode = getStatusCode(status);

            Counter.builder("api.requests.total")
                    .tag("method", method)
                    .tag("path", path)
                    .tag("status", status)
                    .tag("status_code", String.valueOf(statusCode))
                    .register(meterRegistry)
                    .increment();

            Timer.builder("api.response.time")
                    .tag("method", method)
                    .tag("path", path)
                    .tag("status", status)
                    .register(meterRegistry)
                    .record(duration, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            log.warn("메트릭 기록 중 오류 발생: {}", e.getMessage());
        }
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getNormalizedPath(String uri) {
        if (uri.matches("/api/v1/events/\\d+")) {
            return "/api/v1/events/{id}";
        }
        if (uri.matches("/api/v1/orders/\\d+")) {
            return "/api/v1/orders/{id}";
        }
        if (uri.matches("/api/v1/tickets/\\d+")) {
            return "/api/v1/tickets/{id}";
        }
        if (uri.matches("/api/v1/host-channels/\\d+")) {
            return "/api/v1/host-channels/{id}";
        }
        if (uri.matches("/api/v1/hashtags/\\d+")) {
            return "/api/v1/hashtags/{id}";
        }
        if (uri.matches("/api/v1/ticket-options/\\d+")) {
            return "/api/v1/ticket-options/{id}";
        }
        if (uri.matches("/api/v1/reservation-emails/\\d+")) {
            return "/api/v1/reservation-emails/{id}";
        }
        if (uri.matches("/api/v1/events/\\d+/bookmark")) {
            return "/api/v1/events/{id}/bookmark";
        }
        if (uri.matches("/api/v1/events/\\d+/bookmark/\\d+")) {
            return "/api/v1/events/{id}/bookmark/{bookmarkId}";
        }
        
        return uri;
    }

    private int getStatusCode(String status) {
        return "success".equals(status) ? 200 : 500;
    }
} 