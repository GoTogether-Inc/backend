package com.gotogether.domain.ticketoptionassignment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;

public interface TicketOptionAssignmentRepository extends JpaRepository<TicketOptionAssignment, Long> {

	List<TicketOptionAssignment> findAllByTicket(Ticket ticket);

	Optional<TicketOptionAssignment> findByTicketIdAndTicketOptionId(Long ticketId, Long ticketOptionId);

	boolean existsByTicketOption(TicketOption ticketOption);

	List<TicketOptionAssignment> findAllByTicketId(Long ticketId);
}