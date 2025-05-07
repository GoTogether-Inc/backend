package com.gotogether.domain.ticketoption.entity;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.common.entity.BaseEntity;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket_options")
public class TicketOption extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private TicketOptionType type;

	@Column(name = "is_mandatory", nullable = false)
	private boolean isMandatory;

	@Builder
	public TicketOption(Ticket ticket, String name, String description, TicketOptionType type, boolean isMandatory) {
		this.ticket = ticket;
		this.name = name;
		this.description = description;
		this.type = type;
		this.isMandatory = isMandatory;
	}
}