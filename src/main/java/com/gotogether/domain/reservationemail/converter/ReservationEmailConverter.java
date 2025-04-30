package com.gotogether.domain.reservationemail.converter;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.global.util.DateFormatterUtil;

public class ReservationEmailConverter {

	public static ReservationEmail of(ReservationEmailRequestDTO request, Event event) {
		return ReservationEmail.builder()
			.event(event)
			.recipients(request.getRecipients())
			.title(request.getTitle())
			.content(request.getContent())
			.reservationDate(request.getReservationDate())
			.build();
	}

	public static ReservationEmailDetailResponseDTO toReservationEmailDetailResponseDTO(
		ReservationEmail reservationEmail) {
		return ReservationEmailDetailResponseDTO.builder()
			.id(reservationEmail.getId())
			.recipients(reservationEmail.getRecipients())
			.title(reservationEmail.getTitle())
			.content(reservationEmail.getContent())
			.reservationDate(DateFormatterUtil.formatDate(reservationEmail.getReservationDate()))
			.reservationTime(DateFormatterUtil.formatDate(reservationEmail.getReservationDate()))
			.build();
	}
}