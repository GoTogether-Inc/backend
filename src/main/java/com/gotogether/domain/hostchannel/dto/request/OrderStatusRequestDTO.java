package com.gotogether.domain.hostchannel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderStatusRequestDTO {

	@NotNull(message = "orderId는 필수입니다.")
	private Long orderId;
}
