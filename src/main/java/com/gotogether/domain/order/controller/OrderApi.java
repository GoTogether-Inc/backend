package com.gotogether.domain.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.order.dto.request.OrderCancelRequestDTO;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Order", description = "주문 API")
public interface OrderApi {

	@Operation(
		summary = "주문 생성",
		description = "새로운 주문을 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "주문 생성 성공",
			content = @Content(schema = @Schema(implementation = List.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "TICKET4002: 남은 티켓 수량이 부족합니다. / TICKET4003: 이미 종료된 티켓입니다. / TICKET4004: 현재 판매 중인 티켓이 아닙니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다. / TICKET4001: 티켓이 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createOrder(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId,
		@Parameter(description = "주문 생성 요청 데이터", required = true)
		@RequestBody @Valid OrderRequestDTO request
	);

	@Operation(
		summary = "구매한 티켓 목록 조회",
		description = "현재 로그인한 사용자가 구매한 티켓 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "구매한 티켓 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = OrderedTicketResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다."
		)
	})
	@GetMapping
	ApiResponse<List<OrderedTicketResponseDTO>> getPurchasedTickets(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId
	);

	@Operation(
		summary = "구매 확인 조회",
		description = "특정 주문의 구매 확인 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "구매 확인 조회 성공",
			content = @Content(schema = @Schema(implementation = OrderInfoResponseDTO.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "ORDER4001: 주문이 없습니다."
		)
	})
	@GetMapping("/{orderId}/purchase-confirmation")
	ApiResponse<OrderInfoResponseDTO> getPurchaseConfirmation(
		@Parameter(description = "주문 ID", required = true) @PathVariable("orderId") Long orderId
	);

	@Operation(
		summary = "주문 취소",
		description = "기존 주문을 취소합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "주문 취소 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "ORDER4002: 주문과 사용자 정보가 일치하지 않습니다."
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "USER4001: 사용자가 없습니다. / ORDER4001: 주문이 없습니다."
		)
	})
	@PostMapping("/cancel")
	ApiResponse<?> cancelOrder(
		@Parameter(description = "사용자 ID", required = true) @AuthUser Long userId,
		@Parameter(description = "주문 취소 요청 데이터", required = true)
		@RequestBody OrderCancelRequestDTO request
	);

	@Operation(
		summary = "구매자 이메일 조회",
		description = "특정 티켓의 구매자 이메일 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "구매자 이메일 조회 성공",
			content = @Content(schema = @Schema(implementation = TicketPurchaserEmailResponseDTO.class))
		)
	})
	@GetMapping("/purchaser-emails")
	ApiResponse<TicketPurchaserEmailResponseDTO> getPurchaserEmails(
		@Parameter(description = "티켓 ID") @RequestParam(value = "ticketId", required = false) Long ticketId
	);
} 