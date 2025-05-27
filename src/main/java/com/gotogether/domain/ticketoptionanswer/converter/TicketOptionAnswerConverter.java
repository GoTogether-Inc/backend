package com.gotogether.domain.ticketoptionanswer.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.TicketOptionAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.TicketOptionAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;
import com.gotogether.domain.user.entity.User;

public class TicketOptionAnswerConverter {

	public static TicketOptionAnswer of(User user, Order order, TicketOption option, TicketOptionChoice choice,
		String text) {
		return TicketOptionAnswer.builder()
			.user(user)
			.order(order)
			.ticketOption(option)
			.ticketOptionChoice(choice)
			.answerText(text)
			.build();
	}

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
			.optionType(ticketOption.getType().name())
			.answers(response)
			.build();
	}

	public static TicketOptionAnswerDetailResponseDTO toPurchaserAnswerDetailResponseDTO(TicketOptionAnswer answer) {
		String response = answer.getAnswerText() != null
			? answer.getAnswerText()
			: answer.getTicketOptionChoice() != null
			? answer.getTicketOptionChoice().getName()
			: null;

		return TicketOptionAnswerDetailResponseDTO.builder()
			.optionName(answer.getTicketOption().getName())
			.optionType(answer.getTicketOption().getType().name())
			.answer(response)
			.build();
	}
}