package com.gotogether.domain.alert.entity;

import java.time.LocalDateTime;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.Column;
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
@Table(name = "alert")
public class Alert extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "reservation_date", nullable = false)
	private LocalDateTime reservationDate;

	@Column(name = "content", nullable = false)
	private String content;

	@Builder
	public Alert(Event event, String title, LocalDateTime reservationDate, String content) {
		this.event = event;
		this.title = title;
		this.reservationDate = reservationDate;
		this.content = content;
	}
}
