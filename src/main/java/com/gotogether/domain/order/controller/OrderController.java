package com.gotogether.domain.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.order.dto.request.OrderCancelRequestDTO;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.service.OrderService;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ApiResponse<?> createOrder(
		@AuthUser Long userId,
		@RequestBody @Valid OrderRequestDTO request) {
		List<Order> orders = orderService.createOrder(request, userId);

		List<Long> orderIds = orders.stream()
			.map(Order::getId)
			.toList();

		return ApiResponse.onSuccessCreated(orderIds);
	}

	@GetMapping
	public ApiResponse<List<OrderedTicketResponseDTO>> getPurchasedTickets(
		@AuthUser Long userId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<OrderedTicketResponseDTO> purchasedTickets = orderService.getPurchasedTickets(userId, pageable);
		return ApiResponse.onSuccess(purchasedTickets.getContent());
	}

	@GetMapping("/{orderId}/purchase-confirmation")
	public ApiResponse<OrderInfoResponseDTO> getPurchaseConfirmation(
		@PathVariable("orderId") Long orderId) {
		return ApiResponse.onSuccess(orderService.getPurchaseConfirmation(orderId));
	}

	@PostMapping("/cancel")
	public ApiResponse<?> cancelOrder(
		@AuthUser Long userId,
		@RequestBody OrderCancelRequestDTO request) {
		orderService.cancelOrder(request, userId);
		return ApiResponse.onSuccess("주문 취소 성공");
	}

	@GetMapping("/purchaser-emails")
	public ApiResponse<TicketPurchaserEmailResponseDTO> getPurchaserEmails(
		@RequestParam(value = "ticketId", required = false) Long ticketId) {
		TicketPurchaserEmailResponseDTO purchaserEmails = orderService.getPurchaserEmails(ticketId);
		return ApiResponse.onSuccess(purchaserEmails);
	}
}