package com.gotogether.domain.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDTO {

	@NotNull(message = "티켓 ID는 필수입니다.")
	private Long ticketId;

	@NotNull(message = "이벤트 ID는 필수입니다.")
	private Long eventId;

	@Min(value = 1, message = "티켓 개수는 1개 이상이어야 합니다.")
	private int ticketCnt;
}