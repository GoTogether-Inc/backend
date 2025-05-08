package com.gotogether.domain.reservationemail.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEmailDetailResponseDTO {
	private Long id;
	private String targetName;
	private List<String> recipients;
	private String title;
	private String content;
	private String reservationDate;
	private String reservationTime;
}
