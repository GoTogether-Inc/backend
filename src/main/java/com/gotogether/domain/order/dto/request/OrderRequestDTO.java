package com.gotogether.domain.order.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDTO {

	@NotNull(message = "ticketId는 필수입니다.")
	private Long ticketId;

	@NotNull(message = "eventId는 필수입니다.")
	private Long eventId;

	@Min(value = 1, message = "티켓 개수는 최소 1장 이상이어야 합니다.")
	@Max(value = 4, message = "티켓 개수는 최대 4장까지만 구매할 수 있습니다.")
	private int ticketCnt;
}