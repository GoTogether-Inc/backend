package com.gotogether.domain.ticketoption.service;

import java.util.List;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionPerTicketResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;

public interface TicketOptionService {

	TicketOption createTicketOption(TicketOptionRequestDTO request);

	void assignTicketOption(Long ticketOptionId, Long ticketId);

	List<TicketOptionPerTicketResponseDTO> getTicketOptionsPerTicket(Long userId);

	TicketOption updateTicketOption(Long ticketOptionId, TicketOptionRequestDTO request);
}