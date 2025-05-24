package com.gotogether.domain.ticketoption.service;

import java.util.List;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;

public interface TicketOptionService {

	TicketOption createTicketOption(TicketOptionRequestDTO request);

	List<TicketOptionDetailResponseDTO> getTicketOptionsByEventId(Long eventId);

	List<TicketOptionDetailResponseDTO> getTicketOptionsByTicketId(Long ticketId);

	TicketOptionDetailResponseDTO getTicketOption(Long ticketOptionId);

	TicketOption updateTicketOption(Long ticketOptionId, TicketOptionRequestDTO request);

	void deleteTicketOption(Long ticketOptionId);
}