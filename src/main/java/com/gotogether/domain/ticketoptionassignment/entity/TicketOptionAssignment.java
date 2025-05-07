package com.gotogether.domain.ticketoptionassignment.entity;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.global.common.entity.BaseEntity;

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
@Table(name = "ticket_option_assignments")
public class TicketOptionAssignment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_option_id", nullable = false)
	private TicketOption ticketOption;

	@Builder
	public TicketOptionAssignment(Ticket ticket, TicketOption ticketOption) {
		this.ticket = ticket;
		this.ticketOption = ticketOption;
	}
}