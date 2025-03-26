package com.gotogether.global.oauth.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gotogether.domain.user.dto.request.UserDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.oauth.dto.CustomOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;

	public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._AUTHORIZATION_HEADER_MISSING);
			return;
		}

		String token = authorizationHeader.substring(7);

		if (jwtUtil.isExpired(token)) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_EXPIRED);
			return;
		}

		String tokenType = jwtUtil.getTokenType(token);

		if (request.getRequestURI().equals("/api/v1/oauth/reissue")) {
			if (!"refresh".equals(tokenType)) {
				ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_TYPE_ERROR);
				return;
			}

		} else if (!"access".equals(tokenType)) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_TYPE_ERROR);
			return;
		}

		String providerId = jwtUtil.getProviderId(token);

		User user = userRepository.findByProviderId(providerId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		UserDTO userDTO = UserDTO.builder()
			.id(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.provider(user.getProvider())
			.providerId(providerId)
			.build();

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
			customOAuth2User.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}