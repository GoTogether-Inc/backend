package com.gotogether.domain.order.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderedTicketResponseDTO {
	private Long id;
	private Long eventId;
	private String bannerImageUrl;
	private String title;
	private String hostChannelName;
	private String startDate;
	private String location;
	private String ticketName;
	private String ticketStatus;
	private String remainDays;
}
