package com.gotogether.domain.order.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderInfoResponseDTO {
	private Long id;
	private String title;
	private String startDate;
	private String startTime;
	private String ticketName;
	private int ticketCnt;
	private String hostChannelName;
	private String hostChannelDescription;
	private String organizerEmail;
	private String organizerPhoneNumber;
	private String eventAddress;
	private Map<String, Double> location;
	private String orderStatus;
}