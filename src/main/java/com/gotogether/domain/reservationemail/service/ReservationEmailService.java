package com.gotogether.domain.reservationemail.service;

import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;

public interface ReservationEmailService {
    ReservationEmail createReservationEmail(ReservationEmailRequestDTO request);
}