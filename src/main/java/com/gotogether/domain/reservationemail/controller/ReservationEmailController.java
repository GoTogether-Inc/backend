package com.gotogether.domain.reservationemail.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.service.ReservationEmailService;
import com.gotogether.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation-emails")
public class ReservationEmailController {

	private final ReservationEmailService reservationEmailService;

	@PostMapping
	public ApiResponse<?> createReservationEmail(
		@RequestBody @Valid ReservationEmailRequestDTO request) {
		ReservationEmail reservationEmail = reservationEmailService.createReservationEmail(request);
		return ApiResponse.onSuccessCreated(reservationEmail.getId());
	}

	@GetMapping
	public ApiResponse<?> getReservationEmails(
		@RequestParam Long eventId,
		@RequestParam(required = false) String status) {
		List<ReservationEmailDetailResponseDTO> reservationEmailList = reservationEmailService.getReservationEmails(
			eventId, status);
		return ApiResponse.onSuccess(reservationEmailList);
	}

	@PutMapping("/{reservationEmailId}")
	public ApiResponse<?> updateReservationEmail(
		@PathVariable Long reservationEmailId,
		@RequestBody @Valid ReservationEmailRequestDTO request) {
		ReservationEmail reservationEmail = reservationEmailService.updateReservationEmail(reservationEmailId, request);
		return ApiResponse.onSuccess(reservationEmail.getId());
	}

	@DeleteMapping("/{reservationEmailId}")
	public ApiResponse<?> deleteReservationEmail(
		@PathVariable Long reservationEmailId) {
		reservationEmailService.deleteReservationEmail(reservationEmailId);
		return ApiResponse.onSuccess("이벤트에 대한 예약 알림 삭제 성공");
	}
}