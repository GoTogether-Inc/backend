package com.gotogether.domain.ticketqrcode.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gotogether.domain.ticketqrcode.dto.request.ValidateQrCodeRequestDTO;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "TicketQrCode", description = "티켓 QR 코드 API")
public interface TicketQrCodeApi {

	@Operation(
		summary = "QR 코드 검증",
		description = "티켓 QR 코드의 유효성을 검증합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "QR 코드 검증 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "QR_CODE4001: 이미 사용된 QR 코드입니다. / QR_CODE4002: 잘못된 QR 코드 형식입니다. / QR_CODE4003: QR 코드 서명이 유효하지 않습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "ORDER4001: 주문을 찾을 수 없습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "QR_CODE5001: QR 코드 생성에 실패하였습니다."
		)
	})
	@PostMapping("/validate")
	ApiResponse<?> validateQrCode(
		@Parameter(description = "QR 코드 검증 요청 데이터", required = true)
		@RequestBody @Valid ValidateQrCodeRequestDTO request
	);
}