package com.gotogether.domain.ticketoption.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionChoiceResponseDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;

public class TicketOptionConverter {

	public static TicketOption of(TicketOptionRequestDTO request) {
		return TicketOption.builder()
			.eventId(request.getEventId())
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

	public static TicketOptionDetailResponseDTO toTicketOptionDetailResponseDTO(TicketOption ticketOption) {
		List<TicketOptionChoiceResponseDTO> choices = ticketOption.getChoices().stream()
			.map(TicketOptionConverter::toTicketOptionChoiceResponseDTO)
			.collect(Collectors.toList());

		return TicketOptionDetailResponseDTO.builder()
			.id(ticketOption.getId())
			.name(ticketOption.getName())
			.description(ticketOption.getDescription())
			.type(ticketOption.getType())
			.isMandatory(ticketOption.isMandatory())
			.choices(choices)
			.build();
	}

	public static TicketOptionChoiceResponseDTO toTicketOptionChoiceResponseDTO(TicketOptionChoice choice) {
		return TicketOptionChoiceResponseDTO.builder()
			.id(choice.getId())
			.name(choice.getName())
			.build();
	}
}