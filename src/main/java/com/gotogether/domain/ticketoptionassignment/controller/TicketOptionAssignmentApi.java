package com.gotogether.domain.ticketoptionassignment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TicketOptionAssignment", description = "티켓 옵션 할당 API")
public interface TicketOptionAssignmentApi {

	@Operation(
		summary = "티켓 옵션 할당",
		description = "특정 티켓에 옵션을 할당합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 옵션 부착 완료"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TICKET_OPTION_ASSIGN4002: 이미 해당 티켓 옵션이 부착되어 있습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET4001: 티켓이 없습니다. / TICKET_OPTION4001: 티켓 옵션이 존재하지 않습니다."
		)
	})
	@PostMapping
	ApiResponse<?> assignTicketOption(
		@Parameter(description = "티켓 ID", required = true) @RequestParam Long ticketId,
		@Parameter(description = "티켓 옵션 ID", required = true) @RequestParam Long ticketOptionId
	);

	@Operation(
		summary = "티켓 옵션 할당 해제",
		description = "특정 티켓에서 옵션 할당을 해제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 옵션 부착 취소 완료"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET_OPTION_ASSIGN4001: 해당 티켓에 부착된 옵션이 없습니다."
		)
	})
	@DeleteMapping
	ApiResponse<?> unassignTicketOption(
		@Parameter(description = "티켓 ID", required = true) @RequestParam Long ticketId,
		@Parameter(description = "티켓 옵션 ID", required = true) @RequestParam Long ticketOptionId
	);
} 