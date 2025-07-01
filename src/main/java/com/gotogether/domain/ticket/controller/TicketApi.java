package com.gotogether.domain.ticket.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ticket", description = "티켓 API")
public interface TicketApi {

	@Operation(
		summary = "티켓 생성",
		description = "새로운 티켓을 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "티켓 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createTicket(
		@Parameter(description = "티켓 생성 요청 데이터", required = true)
		@RequestBody @Valid TicketRequestDTO request
	);

	@Operation(
		summary = "티켓 목록 조회",
		description = "특정 이벤트의 티켓 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = TicketListResponseDTO.class))
		)
	})
	@GetMapping
	ApiResponse<?> getTickets(
		@Parameter(description = "이벤트 ID", required = true) @RequestParam Long eventId
	);

	@Operation(
		summary = "티켓 삭제",
		description = "특정 티켓을 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET4001: 티켓이 없습니다."
		)
	})
	@DeleteMapping("/{ticketId}")
	ApiResponse<?> deleteTicket(
		@Parameter(description = "티켓 ID", required = true) @PathVariable Long ticketId
	);
} 