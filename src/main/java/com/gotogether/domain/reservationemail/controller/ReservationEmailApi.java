package com.gotogether.domain.reservationemail.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "ReservationEmail", description = "예약 이메일 API")
public interface ReservationEmailApi {

	@Operation(
		summary = "예약 이메일 생성",
		description = "새로운 예약 이메일을 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "예약 이메일 생성 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "EVENT4001: 이벤트가 없습니다. / TICKET4001: 티켓이 없습니다."
		)
	})
	@PostMapping
	ApiResponse<?> createReservationEmail(
		@Parameter(description = "예약 이메일 생성 요청 데이터", required = true)
		@RequestBody @Valid ReservationEmailRequestDTO request
	);

	@Operation(
		summary = "예약 이메일 목록 조회",
		description = "특정 이벤트의 예약 이메일 목록을 조회합니다. 상태별 필터링이 가능합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "예약 이메일 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = ReservationEmailDetailResponseDTO.class))
		)
	})
	@GetMapping
	ApiResponse<?> getReservationEmails(
		@Parameter(description = "이벤트 ID", required = true) @RequestParam Long eventId,
		@Parameter(description = "예약 상태 (PENDING/SENT)") @RequestParam(required = false) String status
	);

	@Operation(
		summary = "예약 이메일 수정",
		description = "기존 예약 이메일 정보를 수정합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "예약 이메일 수정 성공",
			content = @Content(schema = @Schema(implementation = Long.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "RESERVATION_EMAIL4001: 예약 메일이 없습니다. / EVENT4001: 이벤트가 없습니다."
		)
	})
	@PutMapping("/{reservationEmailId}")
	ApiResponse<?> updateReservationEmail(
		@Parameter(description = "예약 이메일 ID", required = true) @PathVariable Long reservationEmailId,
		@Parameter(description = "예약 이메일 수정 요청 데이터", required = true)
		@RequestBody @Valid ReservationEmailRequestDTO request
	);

	@Operation(
		summary = "예약 이메일 삭제",
		description = "특정 예약 이메일을 삭제합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "예약 이메일 삭제 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "RESERVATION_EMAIL4001: 예약 메일이 없습니다."
		)
	})
	@DeleteMapping("/{reservationEmailId}")
	ApiResponse<?> deleteReservationEmail(
		@Parameter(description = "예약 이메일 ID", required = true) @PathVariable Long reservationEmailId
	);
} 