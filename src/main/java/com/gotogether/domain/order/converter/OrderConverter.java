package com.gotogether.domain.order.converter;

import org.springframework.stereotype.Component;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.user.entity.User;

@Component
public class OrderConverter {

	public static Order of(User user, Ticket ticket, TicketStatus ticketStatus) {
		return Order.builder()
			.user(user)
			.ticket(ticket)
			.status(ticketStatus)
			.build();
	}
}