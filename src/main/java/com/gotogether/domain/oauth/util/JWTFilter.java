package com.gotogether.domain.oauth.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gotogether.domain.oauth.dto.CustomOAuth2User;
import com.gotogether.domain.user.dto.request.UserDTO;

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

		String username = jwtUtil.getUsername(token);

		UserDTO userDTO = UserDTO.builder()
			.name(username)
			.email(username)
			.build();

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
			customOAuth2User.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}