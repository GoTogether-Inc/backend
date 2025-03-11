package com.gotogether.domain.hostchannel.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantManagementResponseDTO {
	private Long id;
	private Long ticketNumber;
	private String participant;
	private String email;
	private String phoneNumber;
	private String purchaseDate;
	private String ticketName;
	private String isCheckedIn;
	private String isApproved;
}
