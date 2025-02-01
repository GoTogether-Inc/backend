package com.gotogether.domain.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDTO {
	@JsonProperty("ticketId")
	private Long ticketId;

	@JsonProperty("eventId")
	private Long eventId;

	@JsonProperty("ticketCnt")
	private int ticketCnt;
}