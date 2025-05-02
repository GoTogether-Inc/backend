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
		AND o.status != 'CANCELED'
		ORDER BY ABS(DATEDIFF(o.ticket.event.startDate, CURRENT_DATE)) ASC
		""")
	Page<Order> findOrdersByUser(@Param("user") User user, Pageable pageable);

	Page<Order> findByTicketIdInAndStatus(List<Long> ticketIds, OrderStatus status, Pageable pageable);

	Page<Order> findByTicketIdInAndStatusNot(List<Long> ticketIds, OrderStatus status, Pageable pageable);

	@Query("""
		SELECT o
		FROM Order o
		WHERE o.ticket = :ticket
		AND o.status = :status
		""")
	List<Order> findByTicketAndStatus(@Param("ticket") Ticket ticket, @Param("status") OrderStatus status);

	List<Order> findOrderByUserAndTicket(User user, Ticket ticket);

	@Query("""
			SELECT o.user.email
			FROM Order o
			WHERE o.ticket.id = :ticketId
			AND o.status = 'COMPLETED'
		""")
	List<String> findPurchaserEmailsByTicketId(@Param("ticketId") Long ticketId);

	@Query("""
			SELECT DISTINCT o.user.email
			FROM Order o
			WHERE o.ticket.event.id = :eventId
			AND o.status = 'COMPLETED'
		""")
	List<String> findPurchaserEmailsByEventId(@Param("eventId") Long eventId);
}