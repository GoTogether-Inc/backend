package com.gotogether.domain.term.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Term", description = "이용약관 API")
public interface TermApi {

	@Operation(
		summary = "이용약관 동의",
		description = "사용자가 이용약관에 동의합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "이용약관 동의 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TERM4001: 이미 약관에 동의한 사용자입니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createTerm(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId,
		@Parameter(description = "이용약관 동의 요청 데이터", required = true)
		@RequestBody @Valid TermRequestDTO request
	);
} 