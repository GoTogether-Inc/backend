package com.gotogether.domain.ticketoption.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionPerTicketResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.service.TicketOptionService;
import com.gotogether.global.annotation.AuthUser;
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
		return ApiResponse.onSuccessCreated("ticketOptionId: " + ticketOption.getId());
	}

	@PostMapping("/{ticketOptionId}/assign")
	public ApiResponse<?> assignTicketOption(
		@PathVariable Long ticketOptionId,
		@RequestParam("ticketId") Long ticketId) {
		ticketOptionService.assignTicketOption(ticketOptionId, ticketId);
		return ApiResponse.onSuccess("티켓 옵션 부착 성공");
	}

	@GetMapping("/me")
	public ApiResponse<List<TicketOptionPerTicketResponseDTO>> getTicketOptionsPerTicket(
		@AuthUser Long userId) {
		List<TicketOptionPerTicketResponseDTO> ticketOptionPerTicketList = ticketOptionService.getTicketOptionsPerTicket(userId);
		return ApiResponse.onSuccess(ticketOptionPerTicketList);
	}

	@PutMapping("/{ticketOptionId}")
	public ApiResponse<?> updateTicketOption(
		@PathVariable Long ticketOptionId,
		@RequestBody TicketOptionRequestDTO request) {
		TicketOption updatedOption = ticketOptionService.updateTicketOption(ticketOptionId, request);
		return ApiResponse.onSuccess("ticketOptionId: " + updatedOption.getId());
	}
}