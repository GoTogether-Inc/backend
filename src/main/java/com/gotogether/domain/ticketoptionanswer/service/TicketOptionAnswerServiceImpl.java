package com.gotogether.domain.ticketoptionanswer.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionanswer.converter.TicketOptionAnswerConverter;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;
import com.gotogether.domain.ticketoptionanswer.repository.TicketOptionAnswerRepository;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
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
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void createTicketOptionAnswer(Long userId, TicketOptionAnswerRequestDTO request) {
		TicketOption ticketOption = ticketOptionRepository.findById(request.getTicketOptionId())
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		boolean alreadyAnswered = ticketOptionAnswerRepository
			.existsByUserIdAndTicketOptionId(userId, ticketOption.getId());

		if (alreadyAnswered) {
			throw new GeneralException(ErrorStatus._TICKET_OPTION_ANSWER_ALREADY_EXISTS);
		}

		TicketOptionChoice choice = null;
		if (ticketOption.isSelectableType() && request.getTicketOptionChoiceId() != null) {
			choice = ticketOptionChoiceRepository.findById(request.getTicketOptionChoiceId())
				.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_CHOICE_NOT_FOUND));
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		TicketOptionAnswer answer = TicketOptionAnswer.builder()
			.user(user)
			.ticketOption(ticketOption)
			.ticketOptionChoice(choice)
			.answerText(request.getAnswerText())
			.build();

		ticketOptionAnswerRepository.save(answer);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PurchaserAnswerDetailResponseDTO> getAnswersByUserAndTicket(Long userId, Long ticketId) {
		List<TicketOptionAnswer> answers = ticketOptionAnswerRepository.findByUserIdAndTicketId(userId, ticketId);

		return answers.stream()
			.map(TicketOptionAnswerConverter::toPurchaserAnswerDetailResponseDTO)
			.toList();
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