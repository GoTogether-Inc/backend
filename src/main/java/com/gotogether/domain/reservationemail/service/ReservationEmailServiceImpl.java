package com.gotogether.domain.reservationemail.service;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.converter.ReservationEmailConverter;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.repository.ReservationEmailRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationEmailServiceImpl implements ReservationEmailService {

    private final ReservationEmailRepository reservationEmailRepository;
    private final EventFacade eventFacade;

    @Override
    @Transactional
    public ReservationEmail createReservationEmail(ReservationEmailRequestDTO request) {
        Event event = eventFacade.getEventById(request.getEventId());
        ReservationEmail reservationEmail = ReservationEmailConverter.of(request, event);
        reservationEmailRepository.save(reservationEmail);
        return reservationEmail;
    }
}