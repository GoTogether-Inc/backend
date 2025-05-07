package com.gotogether.domain.ticketoption.dto.response;

import java.util.List;

import com.gotogether.domain.ticketoption.entity.TicketOptionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketOptionResponseDTO {

	private Long id;

	private String name;

	private String description;

	private TicketOptionType type;

	private Boolean isMandatory;

	private List<TicketOptionChoiceResponseDTO> choices;
}