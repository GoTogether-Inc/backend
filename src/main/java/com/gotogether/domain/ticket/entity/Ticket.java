package com.gotogether.domain.ticket.entity;

import java.time.LocalDateTime;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "price", nullable = false)
	private int price;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "available_quantity", nullable = false)
	private int availableQuantity;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "status", nullable = false)
	private TicketStatus status;

	@OneToOne(mappedBy = "ticket")
	private TicketQrCode ticketQrCode;

	@Builder
	public Ticket(Event event, String name, int price, String description, int availableQuantity,
		LocalDateTime startDate, LocalDateTime endDate) {
		this.event = event;
		this.name = name;
		this.price = price;
		this.description = description;
		this.availableQuantity = availableQuantity;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = TicketStatus.AVAILABLE;
	}

	public void updateStatus(TicketStatus status) {
		this.status = status;
	}
}
