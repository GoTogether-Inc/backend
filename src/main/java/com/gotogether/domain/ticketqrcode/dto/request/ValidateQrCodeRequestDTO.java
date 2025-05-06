package com.gotogether.domain.ticketqrcode.dto.request;

import lombok.Getter;

@Getter
public class ValidateQrCodeRequestDTO {
	private Long orderId;
	private String sig;
}