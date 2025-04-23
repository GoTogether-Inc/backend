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
import com.gotogether.global.oauth.dto.FirstLoginResponseDTO;
import com.gotogether.global.oauth.dto.TokenDTO;
import com.gotogether.global.oauth.util.JWTUtil;
import com.gotogether.global.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;
	private final String redirectUrl;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public CustomSuccessHandler(UserRepository userRepository, JWTUtil jwtUtil,
		@Value("${app.redirect-url}") String redirectUrl) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User customUser = (CustomOAuth2User)authentication.getPrincipal();
		User user = findUserByProviderId(customUser.getProviderId());

		if (isFirstLogin(user)) {
			handleFirstLogin(response, user);
		} else {
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
		UserDetailResponseDTO userDto = UserDetailResponseDTO.builder()
			.id(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.build();

		FirstLoginResponseDTO dto = FirstLoginResponseDTO.builder()
			.user(userDto)
			.redirect("/join/agreement")
			.build();

		ApiResponse<FirstLoginResponseDTO> apiResponse = ApiResponse.onSuccess(dto);
		String jsonResponse = objectMapper.writeValueAsString(apiResponse);

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(jsonResponse);
	}

	private void handleSuccessLogin(HttpServletResponse response, User user) throws IOException {
		TokenDTO tokenDTO = jwtUtil.generateTokens(user.getProviderId());

		long expiration = jwtUtil.getExpiration(tokenDTO.getRefreshToken()).getTime();

		response.addCookie(CookieUtil.createCookie("accessToken", tokenDTO.getAccessToken(), expiration));
		response.addCookie(CookieUtil.createCookie("refreshToken", tokenDTO.getRefreshToken(), expiration));

		response.sendRedirect(redirectUrl);
	}
}