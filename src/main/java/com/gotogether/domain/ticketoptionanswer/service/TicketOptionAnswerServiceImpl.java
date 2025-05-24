package com.gotogether.domain.ticketoptionanswer.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionanswer.converter.TicketOptionAnswerConverter;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;
import com.gotogether.domain.ticketoptionanswer.repository.TicketOptionAnswerRepository;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketOptionAnswerServiceImpl implements TicketOptionAnswerService {

	private final OrderRepository orderRepository;
	private final TicketOptionRepository ticketOptionRepository;
	private final TicketOptionChoiceRepository ticketOptionChoiceRepository;
	private final TicketOptionAnswerRepository ticketOptionAnswerRepository;
	private final TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Override
	@Transactional
	public void createTicketOptionAnswer(Long userId, TicketOptionAnswerRequestDTO request) {
		TicketOption ticketOption = ticketOptionRepository.findById(request.getTicketOptionId())
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		Order order = orderRepository.findCompletedOrdersByUserId(userId).stream()
			.filter(o -> ticketOptionAssignmentRepository.findAllByTicket(o.getTicket()).stream()
				.anyMatch(assignment -> assignment.getTicketOption().getId().equals(ticketOption.getId())))
			.findFirst()
			.orElseThrow(() -> new GeneralException(ErrorStatus._ORDER_NOT_FOUND));

		TicketOptionChoice choice = null;
		if (ticketOption.isSelectableType() && request.getTicketOptionChoiceId() != null) {
			choice = ticketOptionChoiceRepository.findById(request.getTicketOptionChoiceId())
				.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_CHOICE_NOT_FOUND));
		}

		TicketOptionAnswer answer = TicketOptionAnswer.builder()
			.order(order)
			.ticketOption(ticketOption)
			.ticketOptionChoice(choice)
			.answerText(request.getAnswerText())
			.build();

		ticketOptionAnswerRepository.save(answer);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PurchaserAnswerResponseDTO> getPurchaserAnswers(Long ticketId) {
		Ticket ticket = ticketOptionAssignmentRepository.findAllByTicketId(ticketId).stream()
			.findFirst()
			.map(TicketOptionAssignment::getTicket)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));

		List<TicketOptionAssignment> assignments = ticketOptionAssignmentRepository.findAllByTicket(ticket);

		List<Long> ticketOptionIds = assignments.stream()
			.map(a -> a.getTicketOption().getId())
			.toList();

		Map<Long, List<TicketOptionAnswer>> answersByOptionId =
			ticketOptionAnswerRepository.findByTicketOptionIdIn(ticketOptionIds)
				.stream()
				.collect(Collectors.groupingBy(a -> a.getTicketOption().getId()));

		return assignments.stream()
			.map(assignment -> {
				TicketOption option = assignment.getTicketOption();
				List<TicketOptionAnswer> answers = answersByOptionId.getOrDefault(option.getId(), List.of());
				return TicketOptionAnswerConverter.toPurchaserAnswerResponseDTO(option, answers);
			})
			.toList();
	}
}