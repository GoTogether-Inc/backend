package com.gotogether.domain.order.dto.response;

import com.gotogether.domain.event.dto.response.EventListResponseDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderedTicketResponseDTO {
	private Long id;
	EventListResponseDTO event;
	private String ticketQrCode;
	private String ticketName;
	private int ticketPrice;
	private String orderStatus;
	private boolean isCheckIn;
}
