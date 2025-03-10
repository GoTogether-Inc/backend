package com.gotogether.domain.reservationemail.facade;

import org.springframework.stereotype.Component;

import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.repository.ReservationEmailRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationEmailFacade {

    private final ReservationEmailRepository reservationEmailRepository;

    public ReservationEmail getReservationEmailById(Long reservationEmailId) {
        return reservationEmailRepository.findById(reservationEmailId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._RESERVATION_EMAIL_NOT_FOUND));
    }
}