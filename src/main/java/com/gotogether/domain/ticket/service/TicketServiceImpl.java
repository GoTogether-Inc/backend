package com.gotogether.domain.ticket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.ticket.converter.TicketConverter;
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;
	private final EventFacade eventFacade;

	@Override
	@Transactional
	public Ticket createTicket(TicketRequestDTO request) {
		Event event = eventFacade.getEventById(request.getEventId());
		Ticket ticket = TicketConverter.of(request, event);

		ticketRepository.save(ticket);
		return ticket;
	}
}