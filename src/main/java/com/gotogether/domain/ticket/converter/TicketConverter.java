package com.gotogether.domain.ticket.converter;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;

public class TicketConverter {

	public static Ticket of(TicketRequestDTO request, Event event) {
		return Ticket.builder()
			.type(request.getTicketType())
			.event(event)
			.name(request.getTicketName())
			.description(request.getTicketDescription())
			.price(request.getTicketPrice())
			.availableQuantity(request.getAvailableQuantity())
			.startDate(request.getStartDate())
			.endDate(request.getEndDate())
			.status(TicketStatus.OPEN)
			.build();
	}

	public static TicketListResponseDTO toTicketListResponseDTO(Ticket ticket) {
		return TicketListResponseDTO.builder()
			.ticketId(ticket.getId())
			.ticketName(ticket.getName())
			.ticketDescription(ticket.getDescription())
			.ticketPrice(ticket.getPrice())
			.availableQuantity(ticket.getAvailableQuantity())
			.startDate(ticket.getStartDate())
			.endDate(ticket.getEndDate())
			.build();
	}
}