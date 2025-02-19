package com.gotogether.domain.ticket.converter;

import java.time.LocalTime;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;

public class TicketConverter {

	public static Ticket of(TicketRequestDTO request, Event event) {
		return Ticket.builder()
			.type(request.getTicketType())
			.event(event)
			.name(request.getTicketName())
			.description(request.getTicketDescription())
			.price(request.getTicketPrice())
			.availableQuantity(request.getAvailableQuantity())
			.startDate(request.getStartDate().atTime(LocalTime.parse(request.getStartTime())))
			.endDate(request.getEndDate().atTime(LocalTime.parse(request.getEndTime())))
			.build();
	}

	public static TicketListResponseDTO toTicketListResponseDTO(Ticket ticket) {
		return TicketListResponseDTO.builder()
			.ticketId(ticket.getId())
			.ticketName(ticket.getName())
			.ticketDescription(ticket.getDescription())
			.ticketPrice(ticket.getPrice())
			.availableQuantity(ticket.getAvailableQuantity())
			.build();
	}
}