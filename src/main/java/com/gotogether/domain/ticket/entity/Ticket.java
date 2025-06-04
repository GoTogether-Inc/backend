package com.gotogether.domain.ticket.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tickets")
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

	@Column(name = "description")
	private String description;

	@Column(name = "available_quantity", nullable = false)
	private int availableQuantity;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private TicketType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private TicketStatus status;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<TicketOptionAssignment> ticketOptionAssignments = new ArrayList<>();

	@Builder
	public Ticket(Event event, String name, int price, String description, int availableQuantity,
		LocalDateTime startDate, LocalDateTime endDate, TicketType type, TicketStatus status) {
		this.event = event;
		this.name = name;
		this.price = price;
		this.description = description;
		this.availableQuantity = availableQuantity;
		this.startDate = startDate;
		this.endDate = endDate;
		this.type = type;
		this.status = status;
	}

	public void decreaseAvailableQuantity() {
		this.availableQuantity--;
	}

	public void increaseAvailableQuantity() {
		this.availableQuantity++;
	}

	public void updateStatus(TicketStatus status) {
		this.status = status;
	}
}
