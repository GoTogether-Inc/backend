package com.gotogether.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.user.entity.User;

public interface OrderCustomRepository {

	Page<Order> findByTicketIdsAndStatus(List<Long> ticketIds, String tag, Pageable pageable);

	Page<Order> findByUser(User user, Pageable pageable);
}
