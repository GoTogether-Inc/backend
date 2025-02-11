package com.gotogether.domain.ticketqrcode.service;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;

public interface TicketQrCodeService {
	TicketQrCode createQrCode(Event event, Ticket ticket);

	void deleteQrCode(Long orderId);
}