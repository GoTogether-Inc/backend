package com.gotogether.global.oauth.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.oauth.dto.CustomOAuth2User;
import com.gotogether.global.oauth.dto.TokenDTO;
import com.gotogether.global.oauth.util.JWTUtil;

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
		@Value("${app.redirect-url}") String redirectUrl) {
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

		User user = userRepository.findByProviderId(providerId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		if (user.getPhoneNumber() == null) {

			UserDetailResponseDTO dto = UserDetailResponseDTO.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();

			ApiResponse<UserDetailResponseDTO> apiResponse = ApiResponse.onSuccess(dto);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(apiResponse);

			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(jsonResponse);

			response.sendRedirect(redirectUrl + "/join/agreement");
		} else {

			TokenDTO tokenDTO = jwtUtil.generateTokens(providerId);

			response.addCookie(createCookie("accessToken", tokenDTO.getAccessToken()));
			response.addCookie(createCookie("refreshToken", tokenDTO.getRefreshToken()));

			response.sendRedirect(redirectUrl);
		}
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
