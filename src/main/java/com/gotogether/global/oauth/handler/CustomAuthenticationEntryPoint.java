package com.gotogether.global.oauth.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.oauth.util.ErrorResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		logger.error("인증 실패 - 401 Unauthorized");
		logger.error("요청 URI: {}", request.getRequestURI());
		logger.error("요청 메서드: {}", request.getMethod());
		logger.error("실패 원인: {}", authException.getClass().getSimpleName());
		logger.error("실패 메시지: {}", authException.getMessage());

		ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._UNAUTHORIZED);
	}
}