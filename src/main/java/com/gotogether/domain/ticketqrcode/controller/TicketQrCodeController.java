package com.gotogether.domain.ticketqrcode.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.ticketqrcode.dto.request.ValidateQrCodeRequestDTO;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket-qr-codes")
public class TicketQrCodeController {

	private final TicketQrCodeService ticketQrCodeService;

	@PostMapping("/validate")
	public ApiResponse<?> validateQrCode(
		@RequestBody ValidateQrCodeRequestDTO request) {
		ticketQrCodeService.validateSignedQrCode(request.getOrderId(), request.getSig());
		return ApiResponse.onSuccess("QR 코드 검증 성공");
	}
}