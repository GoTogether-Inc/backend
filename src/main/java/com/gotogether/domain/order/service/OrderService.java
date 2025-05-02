package com.gotogether.domain.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;

public interface OrderService {
	List<Order> createOrder(OrderRequestDTO request, Long userId);

	Page<OrderedTicketResponseDTO> getPurchasedTickets(Long userId, Pageable pageable);

	OrderInfoResponseDTO getPurchaseConfirmation(Long userId, Long ticketId, Long eventId);

	void cancelOrder(Long userId, Long orderId);

	List<TicketPurchaserEmailResponseDTO> getPurchaserEmails(Long eventId, Long ticketId);
}