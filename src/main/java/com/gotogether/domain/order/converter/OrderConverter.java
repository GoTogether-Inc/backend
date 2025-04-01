package com.gotogether.domain.order.converter;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.order.dto.response.OrderDetailResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.util.DateFormatterUtil;
import com.gotogether.global.util.DateUtil;

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

		Event event = order.getTicket().getEvent();

		EventListResponseDTO eventListDTO = EventListResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.hostChannelName(event.getHostChannel().getName())
			.startDate(DateFormatterUtil.formatDate(event.getStartDate()))
			.address(event.getAddress())
			.onlineType(String.valueOf(event.getOnlineType()))

			.hashtags(event.getHashtags().stream()
				.map(Hashtag::getName)
				.toList()
			)

			.remainDays(DateUtil.getDdayStatus(
				LocalDate.from(event.getStartDate()),
				LocalDate.from(event.getEndDate())))
			.build();

		return OrderedTicketResponseDTO.builder()
			.id(order.getId())
			.event(eventListDTO)
			.ticketQrCode(order.getTicketQrCode().getQrCodeImageUrl())
			.ticketName(order.getTicket().getName())
			.orderStatus(order.getStatus().name())
			.isCheckIn(order.getTicketQrCode().getStatus().isCheckIn())
			.build();
	}

	public static OrderDetailResponseDTO toOrderedDetailResponseDTO(
		Order order, Event event, Ticket ticket) {
		return OrderDetailResponseDTO.builder()
			.id(order.getId())
			.ticketQrCode(order.getTicketQrCode().getQrCodeImageUrl())
			.title(event.getTitle())
			.hostChannelName(event.getHostChannel().getName())
			.startDate(DateFormatterUtil.formatDate(event.getStartDate()))
			.eventAddress(event.getAddress())
			.ticketName(order.getTicket().getName())
			.ticketPrice(order.getTicket().getPrice())
			.orderStatus(order.getTicketQrCode().getStatus().name())

			.remainDays(DateUtil.getDdayStatus(
				LocalDate.from(event.getStartDate()),
				LocalDate.from(event.getEndDate())))
			.build();
	}
}