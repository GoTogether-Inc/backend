package com.gotogether.domain.ticketoption.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TicketOption", description = "티켓 옵션 API")
public interface TicketOptionApi {

	@Operation(
		summary = "티켓 옵션 생성",
		description = "새로운 티켓 옵션을 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "티켓 옵션 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		)
	})
	@PostMapping
	ApiResponse<?> createTicketOption(
		@Parameter(description = "티켓 옵션 생성 요청 데이터", required = true)
		@RequestBody TicketOptionRequestDTO request
	);

	@Operation(
		summary = "이벤트별 티켓 옵션 목록 조회",
		description = "특정 이벤트의 모든 티켓 옵션을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "이벤트별 티켓 옵션 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = TicketOptionDetailResponseDTO.class))
		)
	})
	@GetMapping("/events/{eventId}")
	ApiResponse<List<TicketOptionDetailResponseDTO>> getTicketOptionsByEventId(
		@Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId
	);

	@Operation(
		summary = "티켓별 옵션 목록 조회",
		description = "특정 티켓의 모든 옵션을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓별 옵션 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = TicketOptionDetailResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET4001: 티켓이 없습니다."
		)
	})
	@GetMapping("/tickets/{ticketId}")
	ApiResponse<List<TicketOptionDetailResponseDTO>> getTicketOptionsByTicketId(
		@Parameter(description = "티켓 ID", required = true) @PathVariable Long ticketId
	);

	@Operation(
		summary = "티켓 옵션 상세 조회",
		description = "특정 티켓 옵션의 상세 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 옵션 상세 조회 성공",
			content = @Content(schema = @Schema(implementation = TicketOptionDetailResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET_OPTION4001: 티켓 옵션이 존재하지 않습니다."
		)
	})
	@GetMapping("/{ticketOptionId}")
	ApiResponse<?> getTicketOption(
		@Parameter(description = "티켓 옵션 ID", required = true) @PathVariable Long ticketOptionId
	);

	@Operation(
		summary = "티켓 옵션 수정",
		description = "기존 티켓 옵션 정보를 수정합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 옵션 수정 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET_OPTION4001: 티켓 옵션이 존재하지 않습니다."
		)
	})
	@PutMapping("/{ticketOptionId}")
	ApiResponse<?> updateTicketOption(
		@Parameter(description = "티켓 옵션 ID", required = true) @PathVariable Long ticketOptionId,
		@Parameter(description = "티켓 옵션 수정 요청 데이터", required = true)
		@RequestBody TicketOptionRequestDTO request
	);

	@Operation(
		summary = "티켓 옵션 삭제",
		description = "특정 티켓 옵션을 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 옵션 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TICKET_OPTION_ANSWER4001: 이미 응답된 티켓 옵션입니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET_OPTION4001: 티켓 옵션이 존재하지 않습니다."
		)
	})
	@DeleteMapping("/{ticketOptionId}")
	ApiResponse<?> deleteTicketOption(
		@Parameter(description = "티켓 옵션 ID", required = true) @PathVariable Long ticketOptionId
	);
} 