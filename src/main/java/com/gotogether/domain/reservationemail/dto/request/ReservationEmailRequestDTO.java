package com.gotogether.domain.reservationemail.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.gotogether.domain.reservationemail.entity.ReservationEmailTargetType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEmailRequestDTO {

	@NotNull(message = "eventId는 필수입니다.")
	private Long eventId;

	@NotNull(message = "targetType은 필수입니다.")
	private ReservationEmailTargetType targetType;

	/**
	 * TODO: 커스텀 Validator를 만들어 targetType == TICKET일 때만 ticketId가 null이면 안 되도록 체크
	 */
	private Long ticketId;

	@NotEmpty(message = "수신자 목록은 최소 1명 이상이어야 합니다.")
	@Size(max = 100, message = "예약 메일은 최대 100개까지 가능합니다.")
	private List<@Email(message = "올바른 이메일 형식이어야 합니다.") String> recipients;

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	private String content;

	@NotNull(message = "예약 일시는 필수입니다.")
	@FutureOrPresent(message = "예약 일시는 현재 시간 이후여야 합니다.")
	private LocalDateTime reservationDate;
}