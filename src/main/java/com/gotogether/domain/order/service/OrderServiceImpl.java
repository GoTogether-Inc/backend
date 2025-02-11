package com.gotogether.domain.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.order.converter.OrderConverter;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderedDetailResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
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

	//TODO 정렬 리펙토링
	@Override
	@Transactional(readOnly = true)
	public Page<OrderedTicketResponseDTO> getPurchasedTickets(Long userId, Pageable pageable) {
		User user = eventFacade.getUserById(userId);

		Page<Order> orders = orderRepository.findByUserIdSortedByClosestEvent(user, pageable);

		return orders.map(OrderConverter::toOrderedTicketResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderedDetailResponseDTO getDetailOrder(Long userId, Long orderId) {
		User user = eventFacade.getUserById(userId);

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._ORDER_NOT_FOUND));

		Event event = eventFacade.getEventById(order.getTicket().getEvent().getId());

		Ticket ticket = eventFacade.getTicketById(order.getTicket().getId());

		if (!order.getUser().equals(user)) {
			throw new GeneralException(ErrorStatus._ORDER_NOT_MATCH_USER);
		}

		return OrderConverter.toOrderedDetailResponseDTO(order, event, ticket);
	}

	@Override
	@Transactional
	public void cancelOrder(Long userId, Long orderId) {
		User user = eventFacade.getUserById(userId);

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._ORDER_NOT_FOUND));

		Ticket ticket = eventFacade.getTicketById(order.getTicket().getId());

		if (!order.getUser().equals(user)) {
			throw new GeneralException(ErrorStatus._ORDER_NOT_MATCH_USER);
		}

		order.cancelOrder();
		order.pendingTicket();

		ticket.increaseAvailableQuantity();

		ticketQrCodeService.deleteQrCode(orderId);

		orderRepository.save(order);
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
		} else {

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