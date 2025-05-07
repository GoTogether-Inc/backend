package com.gotogether.domain.ticketoption.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketOptionPerTicketResponseDTO {

	private Long ticketId;

	private String ticketName;

	private List<TicketOptionResponseDTO> options;
}