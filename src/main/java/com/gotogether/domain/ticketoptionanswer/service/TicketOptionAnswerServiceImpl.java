package com.gotogether.domain.ticketoptionanswer.service;

import org.springframework.stereotype.Service;

import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;
import com.gotogether.domain.ticketoptionanswer.repository.TicketOptionAnswerRepository;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * TODO: 결제 완료 후, 티켓 옵션에 대한 답변을 OrderId로 연결
 */
@Service
@RequiredArgsConstructor
public class TicketOptionAnswerServiceImpl implements TicketOptionAnswerService {

	private final UserRepository userRepository;
	private final TicketOptionRepository ticketOptionRepository;
	private final TicketOptionChoiceRepository ticketOptionChoiceRepository;
	private final TicketOptionAnswerRepository ticketOptionAnswerRepository;
	private final TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Override
	@Transactional
	public void createTicketOptionAnswer(Long userId, TicketOptionAnswerRequestDTO request) {
		User user = getUser(userId);

		TicketOption ticketOption = ticketOptionRepository.findById(request.getTicketOptionId())
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		TicketOptionChoice choice = null;
		if (ticketOption.isSelectableType() && request.getTicketOptionChoiceId() != null) {
			choice = ticketOptionChoiceRepository.findById(request.getTicketOptionChoiceId())
				.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_CHOICE_NOT_FOUND));
		}

		TicketOptionAnswer answer = TicketOptionAnswer.builder()
			.user(user)
			.ticketOption(ticketOption)
			.ticketOptionChoice(choice)
			.answerText(request.getAnswerText())
			.build();

		ticketOptionAnswerRepository.save(answer);
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}
}