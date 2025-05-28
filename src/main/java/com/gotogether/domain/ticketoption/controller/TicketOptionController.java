package com.gotogether.domain.ticketoption.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.service.TicketOptionService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket-options")
public class TicketOptionController {

	private final TicketOptionService ticketOptionService;

	@PostMapping
	public ApiResponse<?> createTicketOption(
		@RequestBody TicketOptionRequestDTO request) {
		TicketOption ticketOption = ticketOptionService.createTicketOption(request);
		return ApiResponse.onSuccessCreated(ticketOption.getId());
	}

	@GetMapping("/events/{eventId}")
	public ApiResponse<List<TicketOptionDetailResponseDTO>> getTicketOptionsByEventId(
		@PathVariable Long eventId) {
		List<TicketOptionDetailResponseDTO> ticketOptions = ticketOptionService.getTicketOptionsByEventId(eventId);
		return ApiResponse.onSuccess(ticketOptions);
	}

	@GetMapping("/tickets/{ticketId}")
	public ApiResponse<List<TicketOptionDetailResponseDTO>> getTicketOptionsByTicketId(
		@PathVariable Long ticketId) {
		List<TicketOptionDetailResponseDTO> ticketOptions = ticketOptionService.getTicketOptionsByTicketId(ticketId);
		return ApiResponse.onSuccess(ticketOptions);
	}

	@GetMapping("/{ticketOptionId}")
	public ApiResponse<?> getTicketOption(
		@PathVariable Long ticketOptionId) {
		TicketOptionDetailResponseDTO ticketOption = ticketOptionService.getTicketOption(ticketOptionId);
		return ApiResponse.onSuccess(ticketOption);
	}

	@PutMapping("/{ticketOptionId}")
	public ApiResponse<?> updateTicketOption(
		@PathVariable Long ticketOptionId,
		@RequestBody TicketOptionRequestDTO request) {
		TicketOption updatedOption = ticketOptionService.updateTicketOption(ticketOptionId, request);
		return ApiResponse.onSuccess(updatedOption.getId());
	}

	@DeleteMapping("/{ticketOptionId}")
	public ApiResponse<?> deleteTicketOption(
		@PathVariable Long ticketOptionId) {
		ticketOptionService.deleteTicketOption(ticketOptionId);
		return ApiResponse.onSuccess("티켓 옵션 삭제 성공");
	}
}