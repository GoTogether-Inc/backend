package com.gotogether.domain.ticketoptionanswer.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaserAnswerResponseDTO {

	private Long optionId;

	private String optionName;

	private String optionType;

	private List<TicketOptionAnswerResponseDTO> ticketOptionAnswers;
}
