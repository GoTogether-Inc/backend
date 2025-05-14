package com.gotogether.domain.order.repository;

import java.util.List;

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
		    JOIN FETCH o.ticket t
			JOIN FETCH o.ticketQrCode
		    WHERE t.event.id = :eventId AND o.status = :status
		""")
	List<Order> findCompletedOrdersByEventId(@Param("eventId") Long eventId, @Param("status") OrderStatus status);

	List<Order> findOrderByUserAndTicket(User user, Ticket ticket);

	@Query("""
			SELECT DISTINCT o.user.email
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