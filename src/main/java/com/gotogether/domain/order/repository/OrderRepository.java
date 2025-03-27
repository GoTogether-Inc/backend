package com.gotogether.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.user.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("""
		SELECT o
		FROM Order o
		WHERE o.user = :user
		AND o.ticket.event.startDate >= CURRENT_DATE
		ORDER BY ABS(DATEDIFF(o.ticket.event.startDate, CURRENT_DATE)) ASC
		""")
	Page<Order> findByUserIdSortedByClosestEvent(@Param("user") User user, Pageable pageable);

	Page<Order> findByTicketIdInAndStatus(List<Long> ticketIds, OrderStatus status, Pageable pageable);

	Page<Order> findByTicketIdInAndStatusNot(List<Long> ticketIds, OrderStatus status, Pageable pageable);

	@Query("""
		SELECT o
		FROM Order o
		WHERE o.ticket = :ticket
		AND o.status = :status
		""")
	List<Order> findByTicketAndStatus(@Param("ticket") Ticket ticket, @Param("status") OrderStatus status);
}