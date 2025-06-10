package com.gotogether.domain.order.service;

import java.util.List;

import com.gotogether.domain.order.dto.request.OrderCancelRequestDTO;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;

public interface OrderService {
	List<Order> createOrder(OrderRequestDTO request, Long userId);

	List<OrderedTicketResponseDTO> getPurchasedTickets(Long userId);

	OrderInfoResponseDTO getPurchaseConfirmation(Long orderId);

	void cancelOrder(OrderCancelRequestDTO request, Long userId);

	TicketPurchaserEmailResponseDTO getPurchaserEmails(Long ticketId);
}