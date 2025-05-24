package com.gotogether.domain.ticketoptionanswer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.service.TicketOptionAnswerService;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket-option-answers")
public class TicketOptionAnswerController {

	private final TicketOptionAnswerService ticketOptionAnswerService;

	@PostMapping
	public ApiResponse<?> createTicketOptionAnswer(
		@AuthUser Long userId,
		@RequestBody TicketOptionAnswerRequestDTO request) {
		ticketOptionAnswerService.createTicketOptionAnswer(userId, request);
		return ApiResponse.onSuccess("티켓 옵션 응답 등록 완료");
	}

	@GetMapping("/purchaser-answer")
	public ApiResponse<List<PurchaserAnswerResponseDTO>> getPurchaserAnswers(
		@RequestParam Long ticketId) {
		List<PurchaserAnswerResponseDTO> answers = ticketOptionAnswerService.getPurchaserAnswers(ticketId);
		return ApiResponse.onSuccess(answers);
	}
}