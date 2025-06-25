package com.gotogether.domain.ticketoptionanswer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerListResponseDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TicketOptionAnswer", description = "티켓 옵션 응답 API")
public interface TicketOptionAnswerApi {

	@Operation(
		summary = "티켓 옵션 응답 등록",
		description = "사용자가 티켓 옵션에 대한 응답을 등록합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓 옵션 응답 등록 완료"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TICKET_OPTION_ANSWER4002: 해당 티켓 옵션에 대한 응답이 이미 존재합니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다. / TICKET_OPTION4001: 티켓 옵션이 존재하지 않습니다. / TICKET_OPTION_CHOICE4001: 선택지를 찾을 수 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createTicketOptionAnswer(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId,
		@Parameter(description = "티켓 옵션 응답 등록 요청 데이터", required = true)
		@RequestBody TicketOptionAnswerRequestDTO request
	);

	@Operation(
		summary = "티켓별 응답 목록 조회",
		description = "특정 티켓에 대한 구매자들의 응답 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "티켓별 응답 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = PurchaserAnswerDetailResponseDTO.class))
		)
	})
	@GetMapping
	ApiResponse<List<PurchaserAnswerDetailResponseDTO>> getAnswersByUserAndTicket(
		@Parameter(description = "티켓 ID", required = true) @RequestParam Long ticketId
	);

	@Operation(
		summary = "구매자 응답 목록 조회",
		description = "특정 티켓에 대한 구매자 응답 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "구매자 응답 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = PurchaserAnswerListResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "TICKET4001: 티켓이 없습니다."
		)
	})
	@GetMapping("/purchaser-answer")
	ApiResponse<PurchaserAnswerListResponseDTO> getPurchaserAnswers(
		@Parameter(description = "티켓 ID", required = true) @RequestParam Long ticketId
	);
} 