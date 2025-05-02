package com.gotogether.domain.order.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketPurchaserEmailResponseDTO {
	private List<String> email;
}