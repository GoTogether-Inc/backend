package com.gotogether.domain.ticketoption.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;

public class TicketOptionConverter {

	public static TicketOption of(TicketOptionRequestDTO request) {
		return TicketOption.builder()
			.name(request.getName())
			.description(request.getDescription())
			.type(request.getType())
			.isMandatory(request.getIsMandatory())
			.build();
	}

	public static List<TicketOptionChoice> toTicketOptionChoiceList(List<String> choiceNames,
		TicketOption ticketOption) {
		return choiceNames.stream()
			.map(name -> TicketOptionChoice.builder()
				.ticketOption(ticketOption)
				.name(name)
				.build())
			.collect(Collectors.toList());
	}
}