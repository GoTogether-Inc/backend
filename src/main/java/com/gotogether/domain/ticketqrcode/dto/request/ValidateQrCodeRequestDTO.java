package com.gotogether.domain.ticketqrcode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ValidateQrCodeRequestDTO {

	@NotNull(message = "orderId는 필수입니다.")
	private Long orderId;

	@NotBlank(message = "서명은 필수입니다.")
	private String sig;
}