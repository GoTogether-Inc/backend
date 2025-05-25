package com.gotogether.domain.ticketoptionanswer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketOptionAnswerResponseDTO {

	private Long id;

	private String answer;
}