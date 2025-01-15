package com.gotogether.domain.ticket.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.service.TicketService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

	private final TicketService ticketService;

	@PostMapping
	public ApiResponse<?> createTicket(TicketRequestDTO request) {
		Ticket ticket = ticketService.createTicket(request);
		return ApiResponse.onSuccessCreated("ticketId: " + ticket.getId());
	}
}