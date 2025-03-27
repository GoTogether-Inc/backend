package com.gotogether.domain.ticket.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.ticket.converter.TicketConverter;
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.scheduler.EventScheduler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;
	private final EventFacade eventFacade;
	private final EventScheduler eventScheduler;

	@Override
	@Transactional
	public Ticket createTicket(TicketRequestDTO request) {
		Event event = eventFacade.getEventById(request.getEventId());
		Ticket ticket = TicketConverter.of(request, event);

		ticketRepository.save(ticket);

		eventScheduler.scheduleUpdateTicketStatus(ticket.getId(), ticket.getEndDate());

		return ticket;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketListResponseDTO> getTickets(Long eventId) {
		List<Ticket> tickets = ticketRepository.findByEventId(eventId);

		return tickets.stream()
			.map(TicketConverter::toTicketListResponseDTO)
			.toList();
	}

	@Override
	@Transactional
	public void deleteTicket(Long ticketId) {
		Ticket ticket = getTicketById(ticketId);
		ticketRepository.delete(ticket);
	}

	@Override
	@Transactional
	public void updateTicketStatusToCompleted(Long ticketId) {
		Ticket ticket = getTicketById(ticketId);
		ticket.updateStatus(TicketStatus.CLOSE);
	}

	private Ticket getTicketById(Long ticketId) {
		return ticketRepository.findById(ticketId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));
	}
}