package com.gotogether.domain.ticketoptionanswer.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaserAnswerListResponseDTO {

	private int orderCount;

	private List<PurchaserAnswerResponseDTO> ticketOptions;
}