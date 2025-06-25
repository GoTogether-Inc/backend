package com.gotogether.domain.bookmark.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Bookmark", description = "북마크 API")
public interface BookmarkApi {

	@Operation(
		summary = "북마크 생성",
		description = "특정 이벤트에 대한 북마크를 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "북마크 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "BOOKMARK4001: 이미 북마크된 이벤트입니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다. / USER4001: 사용자가 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createBookmark(
		@Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId,
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId
	);

	@Operation(
		summary = "사용자 북마크 목록 조회",
		description = "현재 로그인한 사용자의 모든 북마크 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "북마크 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = EventListResponseDTO.class))
		),
	})
	@GetMapping
	ApiResponse<?> getUserBookmarks(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId
	);

	@Operation(
		summary = "북마크 삭제",
		description = "특정 북마크를 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "북마크 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "BOOKMARK4002: 북마크를 찾을 수 없습니다."
		)
	})
	@DeleteMapping("/{bookmarkId}")
	ApiResponse<?> deleteBookmark(
		@Parameter(description = "북마크 ID", required = true) @PathVariable Long bookmarkId
	);
} 