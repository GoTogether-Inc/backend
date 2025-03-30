package com.gotogether.domain.order.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.order.dto.response.OrderDetailResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
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
		return OrderedTicketResponseDTO.builder()
			.id(order.getId())
			.eventId(order.getTicket().getEvent().getId())
			.bannerImageUrl(order.getTicket().getEvent().getBannerImageUrl())
			.title(order.getTicket().getEvent().getTitle())
			.hostChannelName(order.getTicket().getEvent().getHostChannel().getName())
			.startDate(order.getTicket().getEvent().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.address(order.getTicket().getEvent().getAddress())

			.hashtags(order.getTicket().getEvent().getHashtags().stream()
				.map(Hashtag::getName)
				.collect(Collectors.toList()))

			.ticketQrCode(order.getTicketQrCode().getQrCodeImageUrl())
			.ticketName(order.getTicket().getName())
			.orderStatus(order.getStatus().name())
			.isCheckIn(order.getTicketQrCode().getStatus().isCheckIn())

			.remainDays(getDdayStatus(
				LocalDate.from(order.getTicket().getEvent().getStartDate()),
				LocalDate.from(order.getTicket().getEvent().getEndDate())))
			.build();
	}

	private static String getDdayStatus(LocalDate startDate, LocalDate endDate) {
		LocalDate today = LocalDate.now();
		long remainDays = startDate.toEpochDay() - today.toEpochDay();
		long remainDaysEnd = endDate.toEpochDay() - today.toEpochDay();

		if (remainDays > 7) {
			return "false";
		} else if (remainDays > 0) {
			return "D-" + remainDays;
		} else if (remainDays <= 0 && remainDaysEnd >= 0) {
			return "진행중";
		} else {
			return "종료";
		}
	}

	public static OrderDetailResponseDTO toOrderedDetailResponseDTO(
		Order order, Event event, Ticket ticket) {
		return OrderDetailResponseDTO.builder()
			.id(order.getId())
			.ticketQrCode(order.getTicketQrCode().getQrCodeImageUrl())
			.title(event.getTitle())
			.hostChannelName(event.getHostChannel().getName())
			.startDate(event.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.eventAddress(event.getAddress())
			.ticketName(order.getTicket().getName())
			.ticketPrice(order.getTicket().getPrice())
			.orderStatus(order.getTicketQrCode().getStatus().name())
			.remainDays(getDdayStatus(
				LocalDate.from(event.getStartDate()),
				LocalDate.from(event.getEndDate())))
			.build();
	}
}