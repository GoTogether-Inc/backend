package com.gotogether.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User", description = "사용자 API")
public interface UserApi {

	@Operation(
		summary = "사용자 정보 수정",
		description = "사용자의 이름과 전화번호를 수정합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "사용자 정보 수정 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "USER4002: 이미 등록된 전화번호입니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@PutMapping
	ApiResponse<?> updateNameAndPhoneNumber(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId,
		@Parameter(description = "사용자 정보 수정 요청 데이터", required = true)
		@RequestBody @Valid UserRequestDTO request
	);

	@Operation(
		summary = "사용자 상세 정보 조회",
		description = "현재 로그인한 사용자의 상세 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "사용자 상세 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = UserDetailResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@GetMapping
	ApiResponse<UserDetailResponseDTO> getDetailUser(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId
	);
} 