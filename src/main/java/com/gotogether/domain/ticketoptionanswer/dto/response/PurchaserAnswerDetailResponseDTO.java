package com.gotogether.domain.ticketoptionanswer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaserAnswerDetailResponseDTO {

	private String optionName;

	private String answer;
}
