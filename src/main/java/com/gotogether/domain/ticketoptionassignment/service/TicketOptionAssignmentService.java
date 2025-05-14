package com.gotogether.domain.ticketoptionassignment.service;

public interface TicketOptionAssignmentService {

	void assignTicketOption(Long ticketId, Long ticketOptionId);

	void unassignTicketOption(Long ticketId, Long ticketOptionId);
}