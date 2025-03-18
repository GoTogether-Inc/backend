package com.gotogether.domain.reservationemail.service;

import java.util.List;

import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;

public interface ReservationEmailService {
	ReservationEmail createReservationEmail(ReservationEmailRequestDTO request);

	List<ReservationEmailDetailResponseDTO> getReservationEmails(Long eventId, String status);

	ReservationEmail updateReservationEmail(Long reservationEmailId, ReservationEmailRequestDTO request);

	void deleteReservationEmail(Long reservationEmailId);

	void sendReservationEmail(Long reservationEmailId);
}