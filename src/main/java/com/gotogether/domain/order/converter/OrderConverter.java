package com.gotogether.domain.order.converter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
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
		Ticket ticket = order.getTicket();
		TicketQrCode ticketQrCode = order.getTicketQrCode();
		Event event = ticket.getEvent();
		HostChannel hostChannel = event.getHostChannel();

		EventListResponseDTO eventListDTO = EventListResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.hostChannelName(hostChannel.getName())
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
			.ticketQrCode(ticketQrCode.getQrCodeImageUrl())
			.ticketName(ticket.getName())
			.orderStatus(order.getStatus().name())
			.isCheckIn(ticketQrCode.getStatus().isCheckIn())
			.build();
	}

	public static OrderInfoResponseDTO toOrderInfoResponseDTO(Order order) {
		Ticket ticket = order.getTicket();
		Event event = ticket.getEvent();
		HostChannel hostChannel = event.getHostChannel();

		return OrderInfoResponseDTO.builder()
			.id(order.getId())
			.title(event.getTitle())
			.startDate(DateFormatterUtil.formatDate(event.getStartDate()))
			.startTime(DateFormatterUtil.formatTime(event.getStartDate().toLocalTime()))
			.ticketName(ticket.getName())
			.hostChannelName(hostChannel.getName())
			.hostChannelDescription(hostChannel.getDescription())
			.organizerEmail(event.getOrganizerEmail())
			.organizerPhoneNumber(event.getOrganizerPhoneNumber())
			.eventAddress(event.getAddress())
			.location(
				Map.of("lat", event.getLocationLat(),
					"lng", event.getLocationLng()))
			.orderStatus(order.getStatus().name())
			.build();
	}

	public static TicketPurchaserEmailResponseDTO toPurchaserEmailResponseDTO(List<String> emails) {
		return TicketPurchaserEmailResponseDTO.builder()
			.email(emails)
			.build();
	}
}