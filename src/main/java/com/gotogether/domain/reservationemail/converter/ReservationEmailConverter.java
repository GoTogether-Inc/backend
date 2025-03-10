package com.gotogether.domain.reservationemail.converter;

import java.time.format.DateTimeFormatter;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;

public class ReservationEmailConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static ReservationEmail of(ReservationEmailRequestDTO request, Event event) {
        return ReservationEmail.builder()
                .event(event)
                .title(request.getTitle())
                .content(request.getContent())
                .reservationDate(request.getReservationDate())
                .build();
    }
}