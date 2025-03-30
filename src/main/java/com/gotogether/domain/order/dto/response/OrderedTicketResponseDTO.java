package com.gotogether.domain.order.dto.response;

import java.util.List;

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
	private String address;
	private List<String> hashtags;
	private String ticketQrCode;
	private String ticketName;
	private int ticketPrice;
	private String orderStatus;
	private boolean isCheckIn;
	private String remainDays;
}
