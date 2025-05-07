package com.gotogether.domain.ticketoption.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
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
		return ApiResponse.onSuccessCreated("ticketOptionId: " + ticketOption.getId());
	}
}