package com.gotogether.domain.ticketoptionassignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;

public interface TicketOptionAssignmentRepository extends JpaRepository<TicketOptionAssignment, Long> {
	List<TicketOptionAssignment> findAllByTicket(Ticket ticket);
}