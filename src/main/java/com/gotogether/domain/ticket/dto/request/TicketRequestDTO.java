package com.gotogether.domain.ticket.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gotogether.domain.ticket.entity.TicketType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketRequestDTO {

	@NotNull(message = "eventId는 필수입니다.")
	private Long eventId;

	@NotNull(message = "티켓 타입은 필수입니다.")
	private TicketType ticketType;

	@NotBlank(message = "티켓명은 필수입니다.")
	private String ticketName;

	@NotBlank(message = "티켓 설명은 필수입니다.")
	private String ticketDescription;

	@PositiveOrZero(message = "티켓 가격은 0 이상이어야 합니다.")
	private int ticketPrice;

	@Positive(message = "수량은 1 이상이어야 합니다.")
	private int availableQuantity;

	@NotNull(message = "시작 날짜는 필수입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate startDate;

	@NotNull(message = "종료 날짜는 필수입니다.")
	@Future(message = "종료 날짜는 미래여야 합니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endDate;

	@NotBlank(message = "시작 시간은 필수입니다.")
	@Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시작 시간은 HH:mm 형식이어야 합니다.")
	private String startTime;

	@NotBlank(message = "종료 시간은 필수입니다.")
	@Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "종료 시간은 HH:mm 형식이어야 합니다.")
	private String endTime;
}