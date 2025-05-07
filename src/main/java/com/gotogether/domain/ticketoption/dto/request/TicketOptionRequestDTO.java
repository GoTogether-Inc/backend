package com.gotogether.domain.ticketoption.dto.request;

import java.util.List;

import com.gotogether.domain.ticketoption.entity.TicketOptionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketOptionRequestDTO {

	private String name;

	private String description;

	private TicketOptionType type;

	private Boolean isMandatory;

	private List<String> choices;
}