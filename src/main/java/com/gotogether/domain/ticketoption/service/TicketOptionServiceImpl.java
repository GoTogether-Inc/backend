package com.gotogether.domain.ticketoption.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.ticketoption.converter.TicketOptionConverter;
import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.entity.TicketOptionType;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketOptionServiceImpl implements TicketOptionService {

	private final TicketOptionRepository ticketOptionRepository;
	private final TicketOptionChoiceRepository ticketOptionChoiceRepository;

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

	private boolean isSelectableType(TicketOptionType type) {
		return type == TicketOptionType.SINGLE || type == TicketOptionType.MULTIPLE;
	}
}