package com.gotogether.domain.ticket.converter;

import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;

public class TicketConverter {

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