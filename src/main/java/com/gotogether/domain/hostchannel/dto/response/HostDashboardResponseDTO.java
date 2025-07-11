package com.gotogether.domain.hostchannel.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostDashboardResponseDTO {
	private String eventName;
	private boolean isTicket;
	private boolean isTicketOption;
	private String eventStartDate;
	private String eventEndDate;
	private Long totalTicketCnt;
	private Long totalPrice;
}