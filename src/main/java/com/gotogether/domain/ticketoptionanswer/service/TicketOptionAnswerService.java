package com.gotogether.domain.ticketoptionanswer.service;

import java.util.List;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerResponseDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;

public interface TicketOptionAnswerService {

	void createTicketOptionAnswer(Long userId, TicketOptionAnswerRequestDTO request);

	List<TicketOptionAnswer> getPendingAnswersByTicket(Ticket ticket);

	List<PurchaserAnswerDetailResponseDTO> getAnswersByUserAndTicket(Long userId, Long ticketId);

	List<PurchaserAnswerResponseDTO> getPurchaserAnswers(Long ticketId);
}