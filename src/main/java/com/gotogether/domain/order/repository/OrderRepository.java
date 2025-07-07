package com.gotogether.domain.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("""
		    SELECT o
		    FROM Order o
		    JOIN FETCH o.ticket t
		    WHERE t.event.id = :eventId AND o.status = :status
		""")
	List<Order> findCompletedOrdersByEventId(@Param("eventId") Long eventId, @Param("status") OrderStatus status);

	@Query("""
			SELECT o
			FROM Order o
			LEFT JOIN FETCH o.ticketQrCode
			LEFT JOIN FETCH o.ticket t
			LEFT JOIN FETCH t.event e
			LEFT JOIN FETCH e.hostChannel
			WHERE o.id = :orderId
		""")
	Optional<Order> findOrderWithTicketAndEventAndHostById(@Param("orderId") Long orderId);

	@Query("""
			SELECT DISTINCT o.user.email
			FROM Order o
			WHERE o.ticket.id = :ticketId
			AND o.status = 'COMPLETED'
		""")
	List<String> findPurchaserEmailsByTicketId(@Param("ticketId") Long ticketId);

	boolean existsByOrderCode(String orderCode);

	@Query("""
		SELECT COUNT(o)
		FROM Order o
		WHERE o.ticket.event.id = :eventId
		AND o.status <> 'CANCELED'
		""")
	Long countByEventId(@Param("eventId") Long eventId);
}