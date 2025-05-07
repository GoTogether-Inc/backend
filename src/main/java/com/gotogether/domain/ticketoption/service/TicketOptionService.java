package com.gotogether.domain.ticketoption.service;

import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;

public interface TicketOptionService {

	TicketOption createTicketOption(TicketOptionRequestDTO request);

	void assignTicketOption(Long ticketOptionId, Long ticketId);
}