package com.gotogether.domain.ticketoptionanswer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;

@Repository
public interface TicketOptionAnswerRepository extends JpaRepository<TicketOptionAnswer, Long> {

	List<TicketOptionAnswer> findByTicketOptionIdIn(List<Long> ticketOptionIds);

	boolean existsByUserIdAndTicketOptionId(Long userId, Long ticketOptionId);

	@Query("""
			SELECT e FROM TicketOptionAnswer e
			WHERE e.user.id = :userId
			AND e.ticketOption.id IN (
				SELECT assignment.ticketOption.id
				FROM TicketOptionAssignment assignment
				WHERE assignment.ticket.id = :ticketId
			)
		""")
	List<TicketOptionAnswer> findByUserIdAndTicketId(@Param("userId") Long userId, @Param("ticketId") Long ticketId);
}