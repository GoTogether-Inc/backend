package com.gotogether.domain.ticketoptionassignment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticketoptionassignment.service.TicketOptionAssignmentService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket-option-assignments")
public class TicketOptionAssignmentController {

	private final TicketOptionAssignmentService ticketOptionAssignmentService;

	@PostMapping
	public ApiResponse<?> assignTicketOption(
		@RequestParam Long ticketId,
		@RequestParam Long ticketOptionId) {
		ticketOptionAssignmentService.assignTicketOption(ticketId, ticketOptionId);
		return ApiResponse.onSuccess("티켓 옵션 부착 완료");
	}

	@DeleteMapping
	public ApiResponse<?> unassignTicketOption(
		@RequestParam Long ticketId,
		@RequestParam Long ticketOptionId) {
		ticketOptionAssignmentService.unassignTicketOption(ticketId, ticketOptionId);
		return ApiResponse.onSuccess("티켓 옵션 부착 취소 완료");
	}

}