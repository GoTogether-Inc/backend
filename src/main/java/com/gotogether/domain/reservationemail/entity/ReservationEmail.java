package com.gotogether.domain.reservationemail.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "reservation_emails")
public class ReservationEmail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Enumerated(EnumType.STRING)
	@Column(name = "target_type", nullable = false)
	private ReservationEmailTargetType targetType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id")
	private Ticket targetTicket;

	@ElementCollection
	@CollectionTable(name = "reservation_email_recipients", joinColumns = @JoinColumn(name = "reservation_email_id"))
	@Column(name = "recipient_email", nullable = false)
	private List<String> recipients;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "reservation_date", nullable = false)
	private LocalDateTime reservationDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationEmailStatus status;

	@Builder
	public ReservationEmail(Event event, ReservationEmailTargetType targetType, Ticket targetTicket,
		List<String> recipients, String title, String content, LocalDateTime reservationDate) {
		this.event = event;
		this.targetType = targetType;
		this.targetTicket = targetTicket;
		this.recipients = recipients;
		this.title = title;
		this.content = content;
		this.reservationDate = reservationDate;
		this.status = ReservationEmailStatus.PENDING;
	}

	public void update(Event event, ReservationEmailRequestDTO request) {
		this.event = event;
		this.recipients = request.getRecipients();
		this.title = request.getTitle();
		this.content = request.getContent();
		this.reservationDate = request.getReservationDate();
	}

	public void markAsSent() {
		this.status = ReservationEmailStatus.SENT;
	}
}
