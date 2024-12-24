package com.gotogether.domain.eventhashtag.entity;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_hashtag")
public class EventHashtag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hashtag_id", nullable = false)
	private Hashtag hashtag;

	@Builder
	public EventHashtag(Event event, Hashtag hashtag) {
		this.event = event;
		this.hashtag = hashtag;
	}
}