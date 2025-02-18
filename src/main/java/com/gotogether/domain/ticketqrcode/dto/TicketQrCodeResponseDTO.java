package com.gotogether.domain.ticketqrcode.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketQrCodeResponseDTO {
	private Long ticketId;

	private byte[] qrCodeImage;
}