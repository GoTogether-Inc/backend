package com.gotogether.domain.reservationemail.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEmailRequestDTO {

	@NotNull(message = "eventId는 필수입니다.")
	private Long eventId;

	@NotEmpty(message = "수신자 목록은 최소 1명 이상이어야 합니다.")
	@Size(max = 100, message = "예약 메일은 최대 100개까지 가능합니다.")
	private List<String> recipients;

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	private String content;

	@NotNull(message = "예약 날짜는 필수입니다.")
	@FutureOrPresent(message = "예약 날짜는 현재 시간 이후여야 합니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate reservationDate;

	/**
	 * reservationTime 타입 String -> LocalTime으로 변경 후, @FutureOrPresent Validation
	 */
	@NotBlank(message = "예약 시간은 필수입니다.")
	@Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "예약 시간은 HH:mm 형식이어야 합니다.")
	private String reservationTime;
}