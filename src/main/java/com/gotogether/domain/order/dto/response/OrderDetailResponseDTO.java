package com.gotogether.domain.order.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailResponseDTO {
	private Long id;
	private String ticketQrCode;
	private String title;
	private String hostChannelName;
	private String startDate;
	private String eventAddress;
	private String ticketName;
	private int ticketPrice;
	private String orderStatus;
	private String remainDays;
}