package com.gotogether.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDTO {

	@NotNull(message = "티켓 ID는 필수입니다.")
	private Long ticketId;

	@NotNull(message = "이벤트 ID는 필수입니다.")
	private Long eventId;

	@Positive(message = "티켓 개수는 1 이상이어야 합니다.")
	private int ticketCnt;
}