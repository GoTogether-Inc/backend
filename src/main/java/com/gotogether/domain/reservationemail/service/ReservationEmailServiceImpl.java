package com.gotogether.domain.reservationemail.service;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.converter.ReservationEmailConverter;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.facade.ReservationEmailFacade;
import com.gotogether.domain.reservationemail.repository.ReservationEmailRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationEmailServiceImpl implements ReservationEmailService {

    private final ReservationEmailRepository reservationEmailRepository;
    private final ReservationEmailFacade reservationEmailFacade;
    private final EventFacade eventFacade;

    @Override
    @Transactional
    public ReservationEmail createReservationEmail(ReservationEmailRequestDTO request) {
        Event event = eventFacade.getEventById(request.getEventId());
        ReservationEmail reservationEmail = ReservationEmailConverter.of(request, event);
        reservationEmailRepository.save(reservationEmail);
        return reservationEmail;
    }

    @Override
    @Transactional
    public ReservationEmail updateReservationEmail(Long reservationEmailId, ReservationEmailRequestDTO request) {
        ReservationEmail reservationEmail = reservationEmailFacade.getReservationEmailById(reservationEmailId);
        Event event = eventFacade.getEventById(request.getEventId());

        reservationEmail.update(event, request);
        reservationEmailRepository.save(reservationEmail);

        return reservationEmail;
    }
}