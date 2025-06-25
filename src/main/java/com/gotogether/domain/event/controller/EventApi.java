package com.gotogether.domain.event.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Event", description = "이벤트 API")
public interface EventApi {

	@Operation(
		summary = "이벤트 생성",
		description = "새로운 이벤트를 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "이벤트 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "HOST_CHANNEL4001: 호스트 채널이 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createEvent(
		@Parameter(description = "이벤트 생성 요청 데이터", required = true)
		@RequestBody @Valid EventRequestDTO request
	);

	@Operation(
		summary = "이벤트 상세 조회",
		description = "특정 이벤트의 상세 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 상세 조회 성공",
			content = @Content(schema = @Schema(implementation = EventDetailResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다."
		)
	})
	@GetMapping("/{eventId}")
	ApiResponse<EventDetailResponseDTO> getDetailEvent(
		@Parameter(description = "사용자 ID (선택사항)") @RequestParam(required = false) Long userId,
		@Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId
	);

	@Operation(
		summary = "이벤트 수정",
		description = "기존 이벤트 정보를 수정합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 수정 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다."
		)
	})
	@PutMapping("/{eventId}")
	ApiResponse<?> updateEvent(
		@Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId,
		@Parameter(description = "이벤트 수정 요청 데이터", required = true)
		@RequestBody @Valid EventRequestDTO request
	);

	@Operation(
		summary = "이벤트 삭제",
		description = "특정 이벤트를 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다."
		)
	})
	@DeleteMapping("/{eventId}")
	ApiResponse<?> deleteEvent(
		@Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId
	);

	@Operation(
		summary = "이벤트 목록 조회",
		description = "태그별로 이벤트 목록을 조회합니다. (current: 최신 이벤트, popular: 인기 이벤트, deadline: 마감임박 이벤트)"
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = EventListResponseDTO.class))
		)
	})
	@GetMapping
	ApiResponse<List<EventListResponseDTO>> getEvents(
		@Parameter(description = "태그 (current/popular/deadline)")
		@RequestParam(name = "tag", defaultValue = "current") String tag,
		@Parameter(description = "페이지 번호")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(value = "size", defaultValue = "10") int size
	);

	@Operation(
		summary = "이벤트 검색",
		description = "키워드로 이벤트를 검색합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트 검색 성공",
			content = @Content(schema = @Schema(implementation = EventListResponseDTO.class))
		)
	})
	@GetMapping("/search")
	ApiResponse<List<EventListResponseDTO>> getEventsSearch(
		@Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
		@Parameter(description = "페이지 번호")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(value = "size", defaultValue = "10") int size
	);

	@Operation(
		summary = "카테고리별 이벤트 조회",
		description = "특정 카테고리의 이벤트 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "카테고리별 이벤트 조회 성공",
			content = @Content(schema = @Schema(implementation = EventListResponseDTO.class))
		)
	})
	@GetMapping("/categories")
	ApiResponse<List<EventListResponseDTO>> getEventsByCategory(
		@Parameter(description = "카테고리", required = true) @RequestParam(name = "category") Category category,
		@Parameter(description = "페이지 번호")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(value = "size", defaultValue = "10") int size
	);
} 