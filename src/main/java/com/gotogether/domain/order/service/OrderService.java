package com.gotogether.domain.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.entity.Order;

public interface OrderService {
	Order createOrder(OrderRequestDTO request, Long userId);

	Page<OrderedTicketResponseDTO> getPurchasedTickets(Long userId, Pageable pageable);

	void cancelOrder(Long userId, Long orderId);
}