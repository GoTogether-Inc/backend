package com.gotogether.domain.order.service;

import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.entity.Order;

public interface OrderService {
	Order createOrder(OrderRequestDTO request, Long userId);
}