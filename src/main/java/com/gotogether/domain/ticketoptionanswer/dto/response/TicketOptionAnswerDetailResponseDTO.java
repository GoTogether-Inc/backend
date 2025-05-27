package com.gotogether.domain.ticketoptionanswer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketOptionAnswerDetailResponseDTO {

	private String optionName;

	private String optionType;

	private String answer;
}


