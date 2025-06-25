package com.gotogether.global.oauth.controller;

import org.springframework.web.bind.annotation.PostMapping;

import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "OAuth", description = "인증 API")
public interface OAuthAPI {

	@Operation(
		summary = "토큰 재발급",
		description = "액세스 토큰을 재발급합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "토큰 재발급 완료"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TOKEN4001: 토큰이 만료되었습니다. / TOKEN4002: 토큰 타입이 잘못되었습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "TOKEN4003: 로그아웃된 토큰입니다. 다시 로그인해주세요. / TOKEN4004: 토큰이 존재하지 않습니다. 로그인이 필요합니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@PostMapping("/reissue")
	ApiResponse<?> reissue(
		HttpServletResponse response,
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId
	);

	@Operation(
		summary = "로그아웃",
		description = "사용자를 로그아웃 처리하고 토큰을 무효화합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "로그아웃 완료"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TOKEN4001: 토큰이 만료되었습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "TOKEN4003: 로그아웃된 토큰입니다. 다시 로그인해주세요. / TOKEN4004: 토큰이 존재하지 않습니다. 로그인이 필요합니다."
		)
	})
	@PostMapping("/logout")
	ApiResponse<?> logout(
		@Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest request,
		@Parameter(description = "HTTP 응답 객체", required = true) HttpServletResponse response
	);
} 