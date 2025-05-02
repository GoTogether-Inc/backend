package com.gotogether.domain.order.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 디자인 패턴 변경 고려
 */
@Getter
@AllArgsConstructor
public class TicketPurchaserEmailResponseDTO {
	private List<String> email;
}