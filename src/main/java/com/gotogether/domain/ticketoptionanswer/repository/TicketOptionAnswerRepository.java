package com.gotogether.domain.ticketoptionanswer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;

@Repository
public interface TicketOptionAnswerRepository extends JpaRepository<TicketOptionAnswer, Long> {

	List<TicketOptionAnswer> findByTicketOptionIdInAndOrderIsNull(List<Long> ticketOptionIds);

	List<TicketOptionAnswer> findByTicketOptionIdIn(List<Long> ticketOptionIds);

	boolean existsByUserIdAndTicketOptionId(Long userId, Long ticketOptionId);

	@Query("""
			SELECT e FROM TicketOptionAnswer e
			WHERE e.ticketOption.id IN (
				SELECT assignment.ticketOption.id
				FROM TicketOptionAssignment assignment
				WHERE assignment.ticket.id = :ticketId
			)
		""")
	List<TicketOptionAnswer> findByTicketId(@Param("ticketId") Long ticketId);

	boolean existsByTicketOption(TicketOption ticketOption);
}