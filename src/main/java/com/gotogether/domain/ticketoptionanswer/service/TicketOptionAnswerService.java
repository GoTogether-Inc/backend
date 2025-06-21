package com.gotogether.domain.ticketoptionanswer.service;

import java.util.List;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerListResponseDTO;
import com.gotogether.domain.user.entity.User;

public interface TicketOptionAnswerService {

	void createTicketOptionAnswer(Long userId, TicketOptionAnswerRequestDTO request);

	List<PurchaserAnswerDetailResponseDTO> getAnswersByTicket(Long ticketId);

	PurchaserAnswerListResponseDTO getPurchaserAnswers(Long ticketId);

	void createTicketOptionAnswers(User user, List<TicketOptionAnswerRequestDTO> requests, Order order);
}