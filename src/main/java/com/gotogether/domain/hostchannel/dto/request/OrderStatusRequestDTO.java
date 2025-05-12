package com.gotogether.domain.hostchannel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequestDTO {

	@NotNull(message = "orderId는 필수입니다.")
	private Long orderId;
}
