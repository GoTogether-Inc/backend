package com.gotogether.global.oauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.oauth.dto.TokenDTO;
import com.gotogether.global.oauth.service.CustomOAuth2UserService;
import com.gotogether.global.oauth.service.OAuthLogoutService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuthLogoutService oAuthLogoutService;

	@PostMapping("/reissue")
	public ApiResponse<?> reissue(HttpServletRequest request,
		@AuthUser Long userId) {
		TokenDTO dto = customOAuth2UserService.reissue(userId);
		return ApiResponse.onSuccess(dto);
	}

	@PostMapping("/logout")
	public ApiResponse<?> logout(
		HttpServletRequest request
	) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);

		oAuthLogoutService.logout(token);
		return ApiResponse.onSuccess("로그아웃 완료");
	}
}