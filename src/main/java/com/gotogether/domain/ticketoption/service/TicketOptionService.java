package com.gotogether.domain.ticketoption.service;

import java.util.List;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionPerTicketResponseDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;

public interface TicketOptionService {

	TicketOption createTicketOption(TicketOptionRequestDTO request);

	List<TicketOptionPerTicketResponseDTO> getTicketOptionsPerTicket(Long userId);

	TicketOptionDetailResponseDTO getTicketOption(Long ticketOptionId);

	TicketOption updateTicketOption(Long ticketOptionId, TicketOptionRequestDTO request);

	void deleteTicketOption(Long ticketOptionId);
}