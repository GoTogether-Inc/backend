package com.gotogether.domain.ticketoptionassignment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionStatus;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketOptionAssignmentServiceImpl implements TicketOptionAssignmentService {

	private final TicketRepository ticketRepository;
	private final TicketOptionRepository ticketOptionRepository;
	private final TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Override
	@Transactional
	public void assignTicketOption(Long ticketId, Long ticketOptionId) {
		Ticket ticket = ticketRepository.findById(ticketId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));

		TicketOption ticketOption = ticketOptionRepository.findById(ticketOptionId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		if (ticketOption.getStatus() == TicketOptionStatus.CREATED) {
			ticketOption.markAsAssigned();
		}

		TicketOptionAssignment assignment = TicketOptionAssignment.builder()
			.ticket(ticket)
			.ticketOption(ticketOption)
			.build();

		ticketOptionAssignmentRepository.save(assignment);
	}

	@Override
	@Transactional
	public void unassignTicketOption(Long ticketId, Long ticketOptionId) {
		TicketOptionAssignment assignment = ticketOptionAssignmentRepository
			.findByTicketIdAndTicketOptionId(ticketId, ticketOptionId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_ASSIGNMENT_NOT_FOUND));

		TicketOption ticketOption = assignment.getTicketOption();

		ticketOptionAssignmentRepository.delete(assignment);
	}
}