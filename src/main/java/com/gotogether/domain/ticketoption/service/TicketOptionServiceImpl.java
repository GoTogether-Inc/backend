package com.gotogether.domain.ticketoption.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketoption.converter.TicketOptionConverter;
import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.entity.TicketOptionType;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketOptionServiceImpl implements TicketOptionService {

	private final TicketOptionRepository ticketOptionRepository;
	private final TicketOptionChoiceRepository ticketOptionChoiceRepository;
	private final TicketRepository ticketRepository;
	private final TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Override
	@Transactional
	public TicketOption createTicketOption(TicketOptionRequestDTO request) {

		TicketOption ticketOption = TicketOptionConverter.of(request);

		ticketOptionRepository.save(ticketOption);

		if (isSelectableType(request.getType())) {
			List<TicketOptionChoice> choices =
				TicketOptionConverter.toTicketOptionChoiceList(request.getChoices(), ticketOption);
			ticketOptionChoiceRepository.saveAll(choices);
		}

		return ticketOption;
	}

	@Override
	@Transactional
	public void assignTicketOption(Long ticketOptionId, Long ticketId) {
		Ticket ticket = ticketRepository.findById(ticketId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));

		TicketOption ticketOption = ticketOptionRepository.findById(ticketOptionId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		TicketOptionAssignment assignment = TicketOptionAssignment.builder()
			.ticket(ticket)
			.ticketOption(ticketOption)
			.build();

		ticketOptionAssignmentRepository.save(assignment);
	}

	private boolean isSelectableType(TicketOptionType type) {
		return type == TicketOptionType.SINGLE || type == TicketOptionType.MULTIPLE;
	}
}