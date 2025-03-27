package com.gotogether.domain.ticket.service;

import java.util.List;

import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;

public interface TicketService {

	Ticket createTicket(TicketRequestDTO request);

	List<TicketListResponseDTO> getTickets(Long userId);

	void deleteTicket(Long ticketId);

	void updateTicketStatusToCompleted(Long ticketId);
}