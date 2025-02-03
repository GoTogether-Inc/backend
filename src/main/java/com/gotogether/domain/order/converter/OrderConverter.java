package com.gotogether.domain.order.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.user.entity.User;

@Component
public class OrderConverter {

	public static Order of(User user, Ticket ticket, TicketStatus ticketStatus) {
		return Order.builder().user(user).ticket(ticket).status(ticketStatus).build();
	}

	public static OrderedTicketResponseDTO toOrderedTicketResponseDTO(Order order) {
		return OrderedTicketResponseDTO.builder()
			.id(order.getId())
			.eventId(order.getTicket().getEvent().getId())
			.bannerImageUrl(order.getTicket().getEvent().getBannerImageUrl())
			.title(order.getTicket().getEvent().getTitle())
			.hostChannelName(order.getTicket().getEvent().getHostChannel().getName())
			.startDate(order.getTicket().getEvent().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.location(order.getTicket().getEvent().getLocation())
			.ticketName(order.getTicket().getName())
			.ticketStatus(order.getStatus().name())
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
}