package com.gotogether.domain.hashtag.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Hashtag", description = "해시태그 API")
public interface HashtagApi {

	@Operation(
		summary = "해시태그 생성",
		description = "새로운 해시태그를 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "해시태그 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "HASHTAG4002: 해시태그가 이미 존재합니다."
		)
	})
	@PostMapping
	ApiResponse<?> createHashtag(
		@Parameter(description = "해시태그 생성 요청 데이터", required = true)
		@RequestBody @Valid HashtagRequestDTO request
	);

	@Operation(
		summary = "해시태그 목록 조회",
		description = "해시태그 목록을 페이지네이션으로 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "해시태그 목록 조회 성공"
		)
	})
	@GetMapping
	ApiResponse<?> getHashtags(
		@Parameter(description = "페이지 번호")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(value = "size", defaultValue = "10") int size
	);

	@Operation(
		summary = "해시태그 수정",
		description = "기존 해시태그 정보를 수정합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "해시태그 수정 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "HASHTAG4002: 해시태그가 이미 존재합니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HASHTAG4001: 해시태그가 없습니다."
		)
	})
	@PutMapping("/{hashtagId}")
	ApiResponse<?> updateHashtag(
		@Parameter(description = "해시태그 ID", required = true) @PathVariable Long hashtagId,
		@Parameter(description = "해시태그 수정 요청 데이터", required = true)
		@RequestBody @Valid HashtagRequestDTO request
	);

	@Operation(
		summary = "해시태그 삭제",
		description = "특정 해시태그를 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "해시태그 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HASHTAG4001: 해시태그가 없습니다."
		)
	})
	@DeleteMapping("/{hashtagId}")
	ApiResponse<?> deleteHashtag(
		@Parameter(description = "해시태그 ID", required = true) @PathVariable Long hashtagId
	);
} 