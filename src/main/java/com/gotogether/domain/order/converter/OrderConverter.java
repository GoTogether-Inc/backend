package com.gotogether.domain.order.converter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
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
				.distinct()
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

	public static OrderInfoResponseDTO toOrderInfoResponseDTO(Order order) {
		return OrderInfoResponseDTO.builder()
			.id(order.getId())
			.title(order.getTicket().getEvent().getTitle())
			.startDate(DateFormatterUtil.formatDate(order.getTicket().getEvent().getStartDate()))
			.startTime(DateFormatterUtil.formatTime(order.getTicket().getEvent().getStartDate().toLocalTime()))
			.ticketName(order.getTicket().getName())
			.hostChannelName(order.getTicket().getEvent().getHostChannel().getName())
			.hostChannelDescription(order.getTicket().getEvent().getHostChannel().getDescription())
			.organizerEmail(order.getTicket().getEvent().getOrganizerEmail())
			.organizerPhoneNumber(order.getTicket().getEvent().getOrganizerPhoneNumber())
			.eventAddress(order.getTicket().getEvent().getAddress())
			.location(
				Map.of("lat", order.getTicket().getEvent().getLocationLat(),
					"lng", order.getTicket().getEvent().getLocationLng()))
			.orderStatus(order.getStatus().name())
			.build();
	}

	public static TicketPurchaserEmailResponseDTO toPurchaserEmailResponseDTO(List<String> emails) {
		return TicketPurchaserEmailResponseDTO.builder()
			.email(emails)
			.build();
	}
}