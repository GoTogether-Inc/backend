package com.gotogether.domain.ticket.dto.request;

import java.time.LocalDateTime;

import com.gotogether.domain.ticket.entity.TicketType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "시작 일시는 필수입니다.")
	private LocalDateTime startDate;

	@NotNull(message = "종료 일시는 필수입니다.")
	@Future(message = "종료 일시는 미래여야 합니다.")
	private LocalDateTime endDate;
}