package com.gotogether.domain.ticket.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketListResponseDTO {
	private Long ticketId;

	private String ticketName;

	private String ticketDescription;

	private int ticketPrice;

	private int availableQuantity;

	private LocalDateTime startDate;

	private LocalDateTime endDate;
}