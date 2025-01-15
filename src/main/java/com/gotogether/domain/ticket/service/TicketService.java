package com.gotogether.domain.ticket.service;

import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.entity.Ticket;

public interface TicketService {

	Ticket createTicket(TicketRequestDTO request);
}