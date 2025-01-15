package com.gotogether.domain.ticket.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gotogether.domain.ticket.entity.TicketType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketRequestDTO {
	@JsonProperty("eventId")
	private Long eventId;

	@JsonProperty("ticketType")
	private TicketType ticketType;

	@JsonProperty("ticketName")
	private String ticketName;

	@JsonProperty("description")
	private String description;

	@JsonProperty("price")
	private int price;

	@JsonProperty("availableQuantity")
	private int availableQuantity;

	@JsonProperty("startDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate startDate;

	@JsonProperty("endDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endDate;

	@JsonProperty("startTime")
	private String startTime;

	@JsonProperty("endTime")
	private String endTime;
}