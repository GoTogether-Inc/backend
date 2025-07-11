package com.gotogether.global.oauth.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.oauth.dto.CustomOAuth2User;
import com.gotogether.global.oauth.dto.TokenDTO;
import com.gotogether.global.oauth.util.JWTUtil;
import com.gotogether.global.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;
	private final String redirectUrl;

	public CustomSuccessHandler(UserRepository userRepository, JWTUtil jwtUtil,
		@Value("${app.redirect-url}") String redirectUrl) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		logger.info("OAuth 로그인 성공");
		logger.info("요청 URI: {}", request.getRequestURI());
		logger.info("요청 메서드: {}", request.getMethod());
		logger.info("사용자: {}", authentication.getPrincipal());

		CustomOAuth2User customUser = (CustomOAuth2User)authentication.getPrincipal();
		User user = findUserByProviderId(customUser.getProviderId());

		if (isFirstLogin(user)) {
			logger.info("신규 사용자 로그인: {}", user.getEmail());
			handleFirstLogin(response, user);
		} else {
			logger.info("기존 사용자 로그인: {}", user.getEmail());
			handleSuccessLogin(response, user);
		}
	}

	private User findUserByProviderId(String providerId) {
		return userRepository.findByProviderId(providerId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private boolean isFirstLogin(User user) {
		return user.getPhoneNumber() == null;
	}

	private void handleFirstLogin(HttpServletResponse response, User user) throws IOException {
		TokenDTO tokenDTO = jwtUtil.generateTempTokens(user.getProviderId());
		setTokenCookiesAndRedirect(response, tokenDTO, redirectUrl + "new");
	}

	private void handleSuccessLogin(HttpServletResponse response, User user) throws IOException {
		TokenDTO tokenDTO = jwtUtil.generateTokens(user.getProviderId());
		setTokenCookiesAndRedirect(response, tokenDTO, redirectUrl + "existing");
	}

	private void setTokenCookiesAndRedirect(HttpServletResponse response, TokenDTO tokenDTO, String redirectUrl) throws
		IOException {
		long expiration = jwtUtil.getExpiration(tokenDTO.getRefreshToken()).getTime();

		response.addCookie(CookieUtil.createCookie("accessToken", tokenDTO.getAccessToken(), expiration));
		response.addCookie(CookieUtil.createCookie("refreshToken", tokenDTO.getRefreshToken(), expiration));

		response.sendRedirect(redirectUrl);
	}
}