package com.gotogether.global.oauth.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gotogether.domain.user.dto.request.UserDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.constants.Constants;
import com.gotogether.global.oauth.dto.CustomOAuth2User;
import com.gotogether.global.oauth.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;
	private final TokenBlacklistService tokenBlacklistService;

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return Constants.NO_NEED_FILTER_URLS.stream()
			.anyMatch(pattern -> pathMatcher.match(pattern, uri));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		Map<String, String> tokens = extractAccessToken(request);

		if (tokens.isEmpty() || tokens.get("refreshToken") == null) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._UNAUTHORIZED);
			return;
		}

		if (tokens.get("accessToken") == null) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_EXPIRED);
			return;
		}

		if (tokenBlacklistService.isTokenBlacklisted(tokens.get("accessToken"))) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_BLACKLISTED);
			return;
		}

		if (jwtUtil.isExpired(tokens.get("accessToken"))) {
			if (request.getRequestURI().equals("/api/v1/oauth/reissue")) {
				authenticateUser(tokens.get("refreshToken"));
				filterChain.doFilter(request, response);
				return;
			}

			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_EXPIRED);
			return;
		}

		authenticateUser(tokens.get("accessToken"));
		filterChain.doFilter(request, response);
	}

	public Map<String, String> extractAccessToken(HttpServletRequest request) {
		Map<String, String> tokens = new HashMap<>();

		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return null;

		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				tokens.put("accessToken", cookie.getValue());
			} else if ("refreshToken".equals(cookie.getName())) {
				tokens.put("refreshToken", cookie.getValue());
			}
		}
		return tokens;
	}

	private void authenticateUser(String token) {
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
	}
}