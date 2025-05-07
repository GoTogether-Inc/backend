package com.gotogether.domain.reservationemail.converter;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.entity.ReservationEmailTargetType;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.util.DateFormatterUtil;

public class ReservationEmailConverter {

	public static ReservationEmail of(ReservationEmailRequestDTO request, Event event, Ticket ticket) {
		return ReservationEmail.builder()
			.event(event)
			.targetType(request.getTargetType())
			.targetTicket(ticket)
			.recipients(request.getRecipients())
			.title(request.getTitle())
			.content(request.getContent())
			.reservationDate(request.getReservationDate())
			.build();
	}

	public static ReservationEmailDetailResponseDTO toReservationEmailDetailResponseDTO(ReservationEmail reservationEmail) {
		String targetName = reservationEmail.getTargetType() == ReservationEmailTargetType.ALL
			? "전체"
			: reservationEmail.getTargetTicket().getName();

		return ReservationEmailDetailResponseDTO.builder()
			.id(reservationEmail.getId())
			.targetName(targetName)
			.recipients(reservationEmail.getRecipients())
			.title(reservationEmail.getTitle())
			.content(reservationEmail.getContent())
			.reservationDate(DateFormatterUtil.formatDate(reservationEmail.getReservationDate()))
			.reservationTime(DateFormatterUtil.formatDate(reservationEmail.getReservationDate()))
			.build();
	}

}