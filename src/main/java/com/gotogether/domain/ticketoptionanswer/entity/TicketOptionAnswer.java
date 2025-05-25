package com.gotogether.domain.ticketoptionanswer.entity;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.user.entity.User;
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
@Table(name = "ticket_option_answers")
public class TicketOptionAnswer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_option_id", nullable = false)
	private TicketOption ticketOption;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_option_choice_id")
	private TicketOptionChoice ticketOptionChoice;

	@Column(name = "answer_text")
	private String answerText;

	@Builder
	public TicketOptionAnswer(User user, Order order, TicketOption ticketOption, TicketOptionChoice ticketOptionChoice, String answerText) {
		this.user = user;
		this.order = order;
		this.ticketOption = ticketOption;
		this.ticketOptionChoice = ticketOptionChoice;
		this.answerText = answerText;
	}

	public void assignOrder(Order order) {
		this.order = order;
	}
}