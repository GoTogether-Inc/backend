package com.gotogether.domain.ticketoptionanswer.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaserAnswerDetailResponseDTO {
	private Long userId;
	private List<PurchaserOrderAnswerResponseDTO> orders;
}
