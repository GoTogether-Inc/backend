package com.gotogether.domain.ticketoptionanswer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;

@Repository
public interface TicketOptionAnswerRepository extends JpaRepository<TicketOptionAnswer, Long> {

	List<TicketOptionAnswer> findByTicketOptionIdInAndOrderIsNull(List<Long> ticketOptionIds);
}