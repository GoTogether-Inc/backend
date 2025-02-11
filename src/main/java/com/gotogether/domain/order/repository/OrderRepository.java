package com.gotogether.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.user.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o " +
		"WHERE o.user = :user AND o.ticket.event.startDate >= CURRENT_DATE " +
		"ORDER BY ABS(DATEDIFF(o.ticket.event.startDate, CURRENT_DATE)) ASC")
	Page<Order> findByUserIdSortedByClosestEvent(@Param("user") User user, Pageable pageable);
}