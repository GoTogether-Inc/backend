package com.gotogether.domain.oauth.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gotogether.domain.oauth.dto.CustomOAuth2User;
import com.gotogether.domain.user.dto.request.UserDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		if (!jwtUtil.validateAuthorizationHeader(authorizationHeader)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(7);

		if (jwtUtil.isExpired(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Token expired");

			return;
		}

		String tokenType = jwtUtil.getTokenType(token);
		// TODO response json
		if (request.getRequestURI().equals("/api/v1/oauth/reissue")) {
			if (!"refresh".equals(tokenType)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Invalid token type. Expected refresh token.");
				
				return;
			}
		}

		if (!"access".equals(tokenType)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Invalid token type. Expected access token.");

			return;

		}

		String providerId = jwtUtil.getProviderId(token);


		UserDTO userDTO = UserDTO.builder()
			.providerId(providerId)
			.build();

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
			customOAuth2User.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}