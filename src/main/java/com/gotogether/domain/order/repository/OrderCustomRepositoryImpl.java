package com.gotogether.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.event.entity.QEvent;
import com.gotogether.domain.hostchannel.entity.QHostChannel;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.entity.QOrder;
import com.gotogether.domain.ticket.entity.QTicket;
import com.gotogether.domain.ticketqrcode.entity.QTicketQrCode;
import com.gotogether.domain.user.entity.QUser;
import com.gotogether.domain.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Order> findByTicketIdsAndStatus(List<Long> ticketIds, String status, Pageable pageable) {
		QOrder order = QOrder.order;
		QUser user = QUser.user;
		QTicket ticket = QTicket.ticket;
		QTicketQrCode ticketQrCode = QTicketQrCode.ticketQrCode;

		BooleanExpression baseCondition = order.ticket.id.in(ticketIds);

		if ("approved".equals(status)) {
			baseCondition = baseCondition.and(order.status.eq(OrderStatus.COMPLETED));
		} else if ("pending".equals(status)) {
			baseCondition = baseCondition.and(order.status.eq(OrderStatus.PENDING));
		} else {
			baseCondition = baseCondition.and(order.status.ne(OrderStatus.CANCELED));
		}

		List<Order> orders = queryFactory
			.selectFrom(order)
			.join(order.user, user).fetchJoin()
			.join(order.ticket, ticket).fetchJoin()
			.leftJoin(order.ticketQrCode, ticketQrCode).fetchJoin()
			.where(baseCondition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(order.createdAt.desc())
			.fetch();

		long totalCount = queryFactory
			.select(order.count())
			.from(order)
			.where(baseCondition)
			.fetchOne();

		return new PageImpl<>(orders, pageable, totalCount);
	}

	@Override
	public List<Order> findByUser(User user) {
		QOrder order = QOrder.order;
		QTicket ticket = QTicket.ticket;
		QTicketQrCode ticketQrCode = QTicketQrCode.ticketQrCode;
		QUser qUser = QUser.user;
		QEvent event = QEvent.event;
		QHostChannel hostChannel = QHostChannel.hostChannel;

		return queryFactory
			.selectDistinct(order)
			.from(order)
			.join(order.user, qUser)
			.join(order.ticket, ticket).fetchJoin()
			.join(ticket.event, event).fetchJoin()
			.join(event.hostChannel, hostChannel).fetchJoin()
			.leftJoin(order.ticketQrCode, ticketQrCode).fetchJoin()
			.where(
				order.user.eq(user),
				order.status.ne(OrderStatus.CANCELED)
			)
			.orderBy(
				new CaseBuilder()
					.when(event.endDate.after(LocalDateTime.now())).then(1)
					.otherwise(0)
					.desc(),
				order.createdAt.desc()
			)
			.fetch();
	}
}