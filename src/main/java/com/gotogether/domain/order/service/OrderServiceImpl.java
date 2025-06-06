package com.gotogether.domain.order.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.order.converter.OrderConverter;
import com.gotogether.domain.order.dto.request.OrderCancelRequestDTO;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderCustomRepository;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.order.util.OrderCodeGenerator;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.service.TicketOptionAnswerService;
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
	private final TicketOptionAnswerService ticketOptionAnswerService;

	@Override
	@Transactional
	public List<Order> createOrder(OrderRequestDTO request, Long userId) {
		User user = eventFacade.getUserById(userId);
		Ticket ticket = eventFacade.getTicketById(request.getTicketId());

		int ticketCnt = request.getTicketCnt();
		checkTicketAvailableQuantity(ticket, ticketCnt);
		checkTicketStatus(ticket);

		List<Order> orders = new ArrayList<>();

		for (int i = 0; i < ticketCnt; i++) {
			Order order = createTicketOrder(user, ticket);
			orders.add(order);

			List<TicketOptionAnswerRequestDTO> answers =
				(request.getTicketOptionAnswers() != null && i < request.getTicketOptionAnswers().size())
					? request.getTicketOptionAnswers().get(i)
					: Collections.emptyList();

			ticketOptionAnswerService.createTicketOptionAnswers(user, answers, order);
		}

		return orders;
	}

	//TODO 정렬 리펙토링 (HHH90003004)
	@Override
	@Transactional(readOnly = true)
	public Page<OrderedTicketResponseDTO> getPurchasedTickets(Long userId, Pageable pageable) {
		User user = eventFacade.getUserById(userId);

		Page<Order> orders = orderCustomRepository.findByUser(user, pageable);

		return orders.map(OrderConverter::toOrderedTicketResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderInfoResponseDTO getPurchaseConfirmation(Long orderId) {
		Order order = orderRepository.findOrderWithTicketAndEventAndHostById(orderId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._ORDER_NOT_FOUND));

		return OrderConverter.toOrderInfoResponseDTO(order);
	}

	@Override
	@Transactional
	public void cancelOrder(OrderCancelRequestDTO request, Long userId) {
		User user = eventFacade.getUserById(userId);

		for (Long orderId : request.getOrderIds()) {
			Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new GeneralException(ErrorStatus._ORDER_NOT_FOUND));

			if (!order.getUser().equals(user)) {
				throw new GeneralException(ErrorStatus._ORDER_NOT_MATCH_USER);
			}

			Ticket ticket = eventFacade.getTicketById(order.getTicket().getId());

			order.cancelOrder();
			ticket.increaseAvailableQuantity();
			ticketQrCodeService.deleteQrCode(orderId);
			orderRepository.save(order);
		}
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

		String orderCode = generateOrderCode();

		OrderStatus status = (ticket.getType() == TicketType.FIRST_COME)
			? OrderStatus.COMPLETED
			: OrderStatus.PENDING;

		Order order = OrderConverter.of(user, ticket, orderCode, status);
		orderRepository.save(order);

		if (ticket.getType() == TicketType.FIRST_COME && event.getOnlineType() == OnlineType.OFFLINE) {
			TicketQrCode ticketQrCode = ticketQrCodeService.createQrCode(order);
			order.updateTicketQrCode(ticketQrCode);

			orderRepository.save(order);
		}

		ticket.decreaseAvailableQuantity();
		return order;
	}

	private String generateOrderCode() {
		String orderCode;
		do {
			orderCode = OrderCodeGenerator.generate();
		} while (orderRepository.existsByOrderCode(orderCode));
		return orderCode;
	}
}