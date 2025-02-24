package com.gotogether.domain.oauth.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.gotogether.domain.oauth.dto.CustomOAuth2User;
import com.gotogether.domain.oauth.dto.TokenDTO;
import com.gotogether.domain.oauth.util.JWTUtil;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;
	private final String redirectUrl;

	public CustomSuccessHandler(UserRepository userRepository, JWTUtil jwtUtil,
		@Value("${spring.jwt.redirect-url}") String redirectUrl) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws
		IOException,
		ServletException {

		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();

		String providerId = customUserDetails.getProviderId();

		TokenDTO tokenDTO = jwtUtil.generateTokens(providerId);

		User user = userRepository.findByProviderId(providerId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		userRepository.save(user);

		response.addCookie(createCookie("accessToken", tokenDTO.getAccessToken()));
		response.addCookie(createCookie("refreshToken", tokenDTO.getRefreshToken()));
		response.sendRedirect(redirectUrl);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60);
		//cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}
}
