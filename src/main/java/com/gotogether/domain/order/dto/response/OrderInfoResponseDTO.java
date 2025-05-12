package com.gotogether.domain.order.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderInfoResponseDTO {
	private Long id;
	private String title;
	private String startDate;
	private String ticketName;
	private String hostChannelName;
	private String hostChannelDescription;
	private String organizerEmail;
	private String organizerPhoneNumber;
	private String eventAddress;
	private Double locationLat;
	private Double locationLng;
	private String orderStatus;
}