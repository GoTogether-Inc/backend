package com.gotogether.global.oauth.controller;

import static com.gotogether.global.util.CookieUtil.*;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.oauth.service.CustomOAuth2UserService;
import com.gotogether.global.oauth.service.OAuthLogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController implements OAuthAPI {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuthLogoutService oAuthLogoutService;

	@PostMapping("/reissue")
	public ApiResponse<?> reissue(HttpServletResponse response,
		@AuthUser Long userId) {
		customOAuth2UserService.reissue(userId, response);
		return ApiResponse.onSuccess("토큰 재발급 완료");
	}

	@PostMapping("/logout")
	public ApiResponse<?> logout(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Map<String, String> tokens = extractTokensFromCookie(request);
		String token = tokens.get("accessToken");
		oAuthLogoutService.logout(token, response);
		return ApiResponse.onSuccess("로그아웃 완료");
	}
}