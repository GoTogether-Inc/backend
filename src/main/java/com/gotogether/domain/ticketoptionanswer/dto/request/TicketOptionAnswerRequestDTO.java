package com.gotogether.domain.ticketoptionanswer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketOptionAnswerRequestDTO {

	private Long ticketOptionId;

	private String answerText;

	private Long ticketOptionChoiceId;
}