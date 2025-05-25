package com.gotogether.domain.ticketoption.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketoption.converter.TicketOptionConverter;
import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.entity.TicketOptionStatus;
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
	private final TicketOptionAssignmentRepository ticketOptionAssignmentRepository;
	private final TicketRepository ticketRepository;

	@Override
	@Transactional
	public TicketOption createTicketOption(TicketOptionRequestDTO request) {

		TicketOption ticketOption = TicketOptionConverter.of(request);

		ticketOptionRepository.save(ticketOption);

		if (ticketOption.isSelectableType()) {
			List<TicketOptionChoice> choices =
				TicketOptionConverter.toTicketOptionChoiceList(request.getChoices(), ticketOption);
			ticketOptionChoiceRepository.saveAll(choices);
		}

		return ticketOption;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketOptionDetailResponseDTO> getTicketOptionsByEventId(Long eventId) {
		List<TicketOptionStatus> visibleStatuses = List.of(
			TicketOptionStatus.CREATED,
			TicketOptionStatus.ASSIGNED
		);

		List<TicketOption> ticketOptions =
			ticketOptionRepository.findAllByEventIdAndStatusIn(eventId, visibleStatuses);

		return ticketOptions.stream()
			.map(TicketOptionConverter::toTicketOptionDetailResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketOptionDetailResponseDTO> getTicketOptionsByTicketId(Long ticketId) {
		Ticket ticket = ticketRepository.findById(ticketId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_NOT_FOUND));

		List<TicketOptionAssignment> assignments = ticketOptionAssignmentRepository.findAllByTicket(ticket);

		List<TicketOption> ticketOptions = assignments.stream()
			.map(TicketOptionAssignment::getTicketOption)
			.toList();

		return ticketOptions.stream()
			.map(TicketOptionConverter::toTicketOptionDetailResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public TicketOptionDetailResponseDTO getTicketOption(Long ticketOptionId) {
		TicketOption ticketOption = ticketOptionRepository.findById(ticketOptionId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		return TicketOptionConverter.toTicketOptionDetailResponseDTO(ticketOption);
	}

	@Override
	@Transactional
	public TicketOption updateTicketOption(Long ticketOptionId, TicketOptionRequestDTO request) {
		TicketOption ticketOption = ticketOptionRepository.findById(ticketOptionId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		ticketOption.update(
			request.getName(),
			request.getDescription(),
			request.getType(),
			request.getIsMandatory()
		);

		ticketOption.clearChoices();

		if (request.getChoices() != null) {
			request.getChoices().forEach(ticketOption::addChoice);
		}

		return ticketOption;
	}

	@Override
	@Transactional
	public void deleteTicketOption(Long ticketOptionId) {
		TicketOption ticketOption = ticketOptionRepository.findById(ticketOptionId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TICKET_OPTION_NOT_FOUND));

		boolean isAssigned = ticketOptionAssignmentRepository.existsByTicketOption(ticketOption);

		if (isAssigned) {
			ticketOption.markAsDeleted();
		} else {
			ticketOptionRepository.delete(ticketOption);
		}
	}
}