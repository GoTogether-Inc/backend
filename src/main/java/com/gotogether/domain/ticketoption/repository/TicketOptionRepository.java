package com.gotogether.domain.ticketoption.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionStatus;

@Repository
public interface TicketOptionRepository extends JpaRepository<TicketOption, Long> {

	List<TicketOption> findAllByEventIdAndStatusIn(Long eventId, List<TicketOptionStatus> statuses);
}