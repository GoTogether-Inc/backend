package com.gotogether.domain.ticketoptionassignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;

public interface TicketOptionAssignmentRepository extends JpaRepository<TicketOptionAssignment, Long> {
}