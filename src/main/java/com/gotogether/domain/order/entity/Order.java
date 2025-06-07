package com.gotogether.domain.order.entity;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.user.entity.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_code", nullable = false, unique = true)
	private String orderCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private OrderStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@OneToOne(mappedBy = "order")
	private TicketQrCode ticketQrCode;

	@Builder
	public Order(String orderCode, OrderStatus status, User user, Ticket ticket) {
		this.orderCode = orderCode;
		this.status = status;
		this.user = user;
		this.ticket = ticket;
	}

	public void updateTicketQrCode(TicketQrCode ticketQrCode) {
		this.ticketQrCode = ticketQrCode;
		ticketQrCode.updateOrder(this);
	}

	public void cancelOrder() {
		this.status = OrderStatus.CANCELED;
	}

	public void approveOrder() {
		this.status = OrderStatus.COMPLETED;
	}
}