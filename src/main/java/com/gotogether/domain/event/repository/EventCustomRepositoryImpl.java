package com.gotogether.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.EventStatus;
import com.gotogether.domain.event.entity.QEvent;
import com.gotogether.domain.eventhashtag.entity.QEventHashtag;
import com.gotogether.domain.hashtag.entity.QHashtag;
import com.gotogether.domain.hostchannel.entity.QHostChannel;
import com.gotogether.domain.ticket.entity.QTicket;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EventCustomRepositoryImpl implements EventCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Event> findEventsByTag(String tag, Pageable pageable) {
		QEvent event = QEvent.event;
		QTicket ticket = QTicket.ticket;
		QEventHashtag eventHashtag = QEventHashtag.eventHashtag;
		QHashtag hashtag = QHashtag.hashtag;

		BooleanExpression baseCondition = event.endDate.goe(LocalDateTime.now())
			.and(event.status.ne(EventStatus.DELETED));

		List<Long> eventIds = queryFactory
			.select(event.id)
			.from(event)
			.where(baseCondition)
			.orderBy(getSortOrder(event, ticket, tag))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		List<Event> events = queryFactory
			.selectFrom(event)
			.leftJoin(event.hostChannel).fetchJoin()
			.leftJoin(event.eventHashtags, eventHashtag).fetchJoin()
			.leftJoin(eventHashtag.hashtag, hashtag).fetchJoin()
			.where(event.id.in(eventIds))
			.orderBy(getSortOrder(event, ticket, tag))
			.fetch();

		long totalCount = queryFactory
			.select(event.countDistinct())
			.from(event)
			.where(baseCondition)
			.fetchOne();

		return new PageImpl<>(events, pageable, totalCount);
	}

	@Override
	public Optional<Event> findEventWithDetails(Long eventId) {
		QEvent event = QEvent.event;
		QHostChannel hostChannel = QHostChannel.hostChannel;
		QEventHashtag eventHashtag = QEventHashtag.eventHashtag;
		QHashtag hashtag = QHashtag.hashtag;

		Event result = queryFactory
			.selectFrom(event).distinct()
			.leftJoin(event.hostChannel, hostChannel).fetchJoin()
			.leftJoin(event.eventHashtags, eventHashtag).fetchJoin()
			.leftJoin(eventHashtag.hashtag, hashtag).fetchJoin()
			.where(event.id.eq(eventId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	private OrderSpecifier<?> getSortOrder(QEvent event, QTicket ticket, String tag) {
		if ("deadline".equals(tag)) {

			return event.endDate.asc();
		} else if ("popular".equals(tag)) {

			JPAQuery<Long> ticketCountSubquery = queryFactory
				.select(ticket.count())
				.from(ticket)
				.where(ticket.event.eq(event));

			return new OrderSpecifier<>(Order.DESC, ticketCountSubquery);
		} else {
			return event.createdAt.desc();
		}
	}
}
