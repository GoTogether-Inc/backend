package com.gotogether.domain.ticketoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.ticketoption.entity.TicketOption;

@Repository
public interface TicketOptionRepository extends JpaRepository<TicketOption, Long> {
}