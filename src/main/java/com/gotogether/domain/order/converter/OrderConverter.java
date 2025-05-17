package com.gotogether.domain.order.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.converter.EventConverter;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.user.entity.User;

@Component
public class OrderConverter {

	public static Order of(User user, Ticket ticket, OrderStatus status) {
		return Order.builder()
			.user(user)
			.ticket(ticket)
			.status(status)
			.build();
	}

	public static OrderedTicketResponseDTO toOrderedTicketResponseDTO(Order order) {
		Ticket ticket = order.getTicket();
		TicketQrCode ticketQrCode = order.getTicketQrCode();
		Event event = ticket.getEvent();

		EventListResponseDTO eventListDTO = EventConverter.toEventListResponseDTO(event);

		return OrderedTicketResponseDTO.builder()
			.id(order.getId())
			.event(eventListDTO)
			.ticketQrCode(ticketQrCode.getQrCodeImageUrl())
			.ticketName(ticket.getName())
			.orderStatus(order.getStatus().name())
			.isCheckIn(ticketQrCode.getStatus().isCheckIn())
			.build();
	}

	public static OrderInfoResponseDTO toOrderInfoResponseDTO(Order order) {
		Ticket ticket = order.getTicket();
		Event event = ticket.getEvent();

		return OrderInfoResponseDTO.builder()
			.id(order.getId())
			.title(event.getTitle())
			.startDate(String.valueOf(event.getStartDate()))
			.ticketName(ticket.getName())
			.hostChannelName(event.getHostChannel().getName())
			.hostChannelDescription(event.getHostChannel().getDescription())
			.organizerEmail(event.getOrganizerEmail())
			.organizerPhoneNumber(event.getOrganizerPhoneNumber())
			.eventAddress(event.getAddress())
			.locationLat(event.getLocationLat())
			.locationLng(event.getLocationLng())
			.orderStatus(order.getStatus().name())
			.build();
	}

	public static TicketPurchaserEmailResponseDTO toPurchaserEmailResponseDTO(List<String> emails) {
		return TicketPurchaserEmailResponseDTO.builder()
			.email(emails)
			.build();
	}
}