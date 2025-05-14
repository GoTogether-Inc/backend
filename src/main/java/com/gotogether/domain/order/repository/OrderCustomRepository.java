package com.gotogether.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.order.entity.Order;

public interface OrderCustomRepository {

	Page<Order> findByTicketIdsAndStatus(List<Long> ticketIds, String tags, Pageable pageable);
}
