package com.gotogether.domain.reservationemail.controller;

import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.service.ReservationEmailService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation-emails")
public class ReservationEmailController {

    private final ReservationEmailService reservationEmailService;

    @PostMapping
    public ApiResponse<?> createReservationEmail(@RequestBody ReservationEmailRequestDTO request) {
        ReservationEmail reservationEmail = reservationEmailService.createReservationEmail(request);
        return ApiResponse.onSuccessCreated("reservationEmailId: " + reservationEmail.getId());
    }
}