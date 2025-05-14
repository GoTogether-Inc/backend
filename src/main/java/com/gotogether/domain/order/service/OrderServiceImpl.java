package com.gotogether.domain.order.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.order.converter.OrderConverter;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderCustomRepository;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
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
	private final OrderCustomRepository orderCustomRepository;

	@Override
	@Transactional
	public List<Order> createOrder(OrderRequestDTO request, Long userId) {
		User user = eventFacade.getUserById(userId);
		Ticket ticket = eventFacade.getTicketById(request.getTicketId());

		int ticketCnt = request.getTicketCnt();
		checkTicketAvailableQuantity(ticket, ticketCnt);
		checkTicketStatus(ticket);

		return IntStream.range(0, ticketCnt)
			.mapToObj(i -> createTicketOrder(user, ticket))
			.collect(Collectors.toList());
	}

	//TODO 정렬 리펙토링
	@Override
	@Transactional(readOnly = true)
	public Page<OrderedTicketResponseDTO> getPurchasedTickets(Long userId, Pageable pageable) {
		User user = eventFacade.getUserById(userId);

		Page<Order> orders = orderCustomRepository.findByUser(user, pageable);

		return orders.map(OrderConverter::toOrderedTicketResponseDTO);
	}

	@Override
	public OrderInfoResponseDTO getPurchaseConfirmation(Long userId, Long ticketId, Long eventId) {
		User user = eventFacade.getUserById(userId);
		Event event = eventFacade.getEventById(eventId);
		Ticket ticket = eventFacade.getTicketById(ticketId);

		List<Order> orders = orderRepository.findOrderByUserAndTicket(user, ticket);

		return OrderConverter.toOrderInfoResponseDTO(orders.get(0), event);
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

		ticket.increaseAvailableQuantity();

		ticketQrCodeService.deleteQrCode(orderId);

		orderRepository.save(order);
	}

	@Override
	@Transactional(readOnly = true)
	public TicketPurchaserEmailResponseDTO getPurchaserEmails(Long eventId, Long ticketId) {
		List<String> purchaserEmails;

		if (ticketId != null) {
			purchaserEmails = orderRepository.findPurchaserEmailsByTicketId(ticketId);
		} else {
			purchaserEmails = orderRepository.findPurchaserEmailsByEventId(eventId);
		}

		return OrderConverter.toPurchaserEmailResponseDTO(purchaserEmails);
	}

	private void checkTicketAvailableQuantity(Ticket ticket, int ticketCnt) {
		if (ticket.getAvailableQuantity() < ticketCnt) {
			throw new GeneralException(ErrorStatus._TICKET_NOT_ENOUGH);
		}
	}

	private void checkTicketStatus(Ticket ticket) {
		if (ticket.getStatus() == TicketStatus.CLOSE) {
			throw new GeneralException(ErrorStatus._TICKET_ALREADY_CLOSED);
		}
	}

	private Order createTicketOrder(User user, Ticket ticket) {
		Event event = ticket.getEvent();

		OrderStatus status = (ticket.getType() == TicketType.FIRST_COME)
			? OrderStatus.COMPLETED
			: OrderStatus.PENDING;

		Order order = OrderConverter.of(user, ticket, status);
		orderRepository.save(order);

		if (ticket.getType() == TicketType.FIRST_COME && event.getOnlineType() == OnlineType.OFFLINE) {
			TicketQrCode ticketQrCode = ticketQrCodeService.createQrCode(order);
			order.updateTicketQrCode(ticketQrCode);

			orderRepository.save(order);
		}

		ticket.decreaseAvailableQuantity();
		return order;
	}
}