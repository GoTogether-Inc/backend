package com.gotogether.domain.ticketoptionanswer.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketOptionAnswerRequestDTO {

	private Long ticketOptionId;

	private String answerText;

	private Long ticketOptionChoiceId;

	private List<Long> ticketOptionChoiceIds;
}