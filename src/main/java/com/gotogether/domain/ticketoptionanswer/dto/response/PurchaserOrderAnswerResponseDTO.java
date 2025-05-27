package com.gotogether.domain.ticketoptionanswer.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaserOrderAnswerResponseDTO {
	private Long orderId;
	private List<TicketOptionAnswerDetailResponseDTO> optionAnswers;
}
