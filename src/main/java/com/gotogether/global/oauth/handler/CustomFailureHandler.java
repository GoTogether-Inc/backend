package com.gotogether.global.oauth.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.gotogether.global.oauth.exception.DuplicatedEmailException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomFailureHandler.class);

	@Value("${app.redirect-url}")
	String redirectUrl;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		logger.error("OAuth 로그인 실패");
		logger.error("요청 URI: {}", request.getRequestURI());
		logger.error("요청 메서드: {}", request.getMethod());
		logger.error("실패 원인: {}", exception.getClass().getSimpleName());
		logger.error("실패 메시지: {}", exception.getMessage());

		String status;

		if (exception instanceof DuplicatedEmailException) {
			logger.error("중복 이메일로 인한 로그인 실패");
			status = "duplicatedEmail";
		} else {
			logger.error("서버 오류로 인한 로그인 실패");
			status = "serverError";
		}

		String targetUrl = redirectUrl + status;
		logger.info("리다이렉트 URL: {}", targetUrl);

		response.sendRedirect(targetUrl);
	}
}