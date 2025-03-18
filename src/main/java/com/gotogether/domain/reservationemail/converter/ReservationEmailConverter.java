package com.gotogether.domain.reservationemail.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;

public class ReservationEmailConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static ReservationEmail of(ReservationEmailRequestDTO request, Event event) {
        return ReservationEmail.builder()
                .event(event)
                .recipients(request.getRecipients())
                .title(request.getTitle())
                .content(request.getContent())
                .reservationDate(request.getReservationDate().atTime(LocalTime.parse(request.getReservationTime())))
                .build();
    }

    public static ReservationEmailDetailResponseDTO toReservationEmailDetailResponseDTO(ReservationEmail reservationEmail) {
        return ReservationEmailDetailResponseDTO.builder()
                .id(reservationEmail.getId())
                .recipients(reservationEmail.getRecipients())
                .title(reservationEmail.getTitle())
                .content(reservationEmail.getContent())
                .reservationDate(reservationEmail.getReservationDate().format(DATE_FORMATTER))
                .reservationTime(reservationEmail.getReservationDate().format(TIME_FORMATTER))
                .build();
    }
}