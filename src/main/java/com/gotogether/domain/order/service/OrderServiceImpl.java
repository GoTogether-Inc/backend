package com.gotogether.domain.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.order.converter.OrderConverter;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.TicketStatus;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeService;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final EventFacade eventFacade;
	private final TicketQrCodeService ticketQrCodeService;

	@Override
	@Transactional
	public Order createOrder(OrderRequestDTO request, Long userId) {

		User user = eventFacade.getUserById(userId);
		Ticket ticket = eventFacade.getTicketById(request.getTicketId());
		Event event = eventFacade.getEventById(request.getEventId());

		int ticketCnt = request.getTicketCnt();
		checkTicketAvailableQuantity(ticket, ticketCnt);

		Order order = null;
		for (int i = 0; i < ticketCnt; i++) {

			order = createTicketOrder(user, ticket, event);
		}
		return order;
	}

	private void checkTicketAvailableQuantity(Ticket ticket, int ticketCnt) {

		if (ticket.getAvailableQuantity() < ticketCnt) {
			throw new GeneralException(ErrorStatus._TICKET_NOT_ENOUGH);
		}
	}

	private Order createTicketOrder(User user, Ticket ticket, Event event) {

		TicketStatus ticketStatus;
		TicketQrCode ticketQrCode = null;

		if (ticket.getType() == TicketType.FIRST_COME) {

			ticketQrCode = ticketQrCodeService.createQrCode(event, ticket);
			ticketStatus = TicketStatus.AVAILABLE;
		}else{

			ticketStatus = TicketStatus.PENDING;
		}

		Order order = OrderConverter.of(user, ticket, ticketStatus);
		if (ticketQrCode != null) {
			order.updateTicketQrCode(ticketQrCode);
		}
		orderRepository.save(order);
		ticket.decreaseAvailableQuantity();
		return order;
	}
}