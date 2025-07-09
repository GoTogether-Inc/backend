package com.gotogether.global.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gotogether.global.common.dto.SmsRequestDTO;
import com.gotogether.global.common.dto.SmsVerifyRequestDTO;

import jakarta.validation.Valid;

@Tag(name = "SMS", description = "전화번호 SMS 인증 API")
public interface SmsApi {

    @Operation(
        summary = "SMS 인증번호 발송",
        description = "휴대폰 번호로 SMS 인증번호를 발송합니다. 인증번호는 3분간 유효합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "인증번호 발송 성공",
            content = @Content(schema = @Schema(implementation = com.gotogether.global.apipayload.ApiResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "SMS4001: 이미 인증 코드가 발송되었습니다.",
            content = @Content(schema = @Schema(implementation = com.gotogether.global.apipayload.ApiResponse.class))
        )
    })
    @PostMapping("/send")
    com.gotogether.global.apipayload.ApiResponse<?> sendSms(
        @Parameter(
            description = "SMS 인증번호 발송 요청",
            required = true,
            content = @Content(schema = @Schema(implementation = SmsRequestDTO.class))
        )
        @Valid @RequestBody SmsRequestDTO dto
    );

    @Operation(
        summary = "SMS 인증번호 검증",
        description = "휴대폰 번호와 인증번호를 확인하여 인증을 완료합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "전화번호 인증 성공",
            content = @Content(schema = @Schema(implementation = com.gotogether.global.apipayload.ApiResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "SMS4002: 인증 코드가 만료되었습니다. / SMS4003: 인증 코드가 일치하지 않습니다.",
            content = @Content(schema = @Schema(implementation = com.gotogether.global.apipayload.ApiResponse.class))
        )
    })
    @PostMapping("/verify")
    com.gotogether.global.apipayload.ApiResponse<?> verifySms(
        @Parameter(
            description = "SMS 인증번호 검증 요청",
            required = true,
            content = @Content(schema = @Schema(implementation = SmsVerifyRequestDTO.class))
        )
        @Valid @RequestBody SmsVerifyRequestDTO dto
    );
}