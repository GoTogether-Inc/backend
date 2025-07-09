package com.gotogether.global.common.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.common.dto.SmsRequestDTO;
import com.gotogether.global.common.dto.SmsVerifyRequestDTO;
import com.gotogether.global.common.service.SmsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sms")
public class SmsController implements SmsApi {

	private final SmsService smsService;

	@Override
	@PostMapping("/send")
	public ApiResponse<?> sendSms(@Valid @RequestBody SmsRequestDTO request) {
		smsService.sendCertificationCode(request);
		return ApiResponse.onSuccess("인증번호 발송 성공");
	}

	@Override
	@PostMapping("/verify")
	public ApiResponse<?> verifySms(@Valid @RequestBody SmsVerifyRequestDTO request) {
		smsService.verifyCertificationCode(request);
		return ApiResponse.onSuccess("전화번호 인증 성공");
	}
}