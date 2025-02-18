package com.gotogether.domain.order.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderedDetailResponseDTO {
	private Long id;
	private String ticketQrCode;
	private String title;
	private String hostChannelName;
	private String startDate;
	private String location;
	private String ticketName;
	private int ticketPrice;
	private String ticketStatus;
	private String remainDays;
}