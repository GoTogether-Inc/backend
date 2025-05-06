package com.gotogether.domain.ticketqrcode.service;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;

public interface TicketQrCodeService {
	TicketQrCode createQrCode(Order order);

	void deleteQrCode(Long orderId);
}