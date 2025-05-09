package com.gotogether.domain.ticketoptionanswer.service;

import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;

public interface TicketOptionAnswerService {

	void createTicketOptionAnswer(Long userId, TicketOptionAnswerRequestDTO request);
}