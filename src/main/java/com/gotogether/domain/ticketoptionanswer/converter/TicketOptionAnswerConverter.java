package com.gotogether.domain.ticketoptionanswer.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.TicketOptionAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;

public class TicketOptionAnswerConverter {

	public static TicketOptionAnswerResponseDTO toTicketOptionAnswerResponseDTO(TicketOptionAnswer answer) {
		String answers = (answer.getAnswerText() != null)
			? answer.getAnswerText()
			: answer.getTicketOptionChoice().getName();

		return TicketOptionAnswerResponseDTO.builder()
			.id(answer.getId())
			.answer(answers)
			.build();
	}

	public static PurchaserAnswerResponseDTO toPurchaserAnswerResponseDTO(
		TicketOption ticketOption,
		List<TicketOptionAnswer> answers) {
		List<TicketOptionAnswerResponseDTO> response = answers.stream()
			.map(TicketOptionAnswerConverter::toTicketOptionAnswerResponseDTO)
			.collect(Collectors.toList());

		return PurchaserAnswerResponseDTO.builder()
			.optionId(ticketOption.getId())
			.optionName(ticketOption.getName())
			.answers(response)
			.build();
	}
}