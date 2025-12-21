package com.gotogether.domain.order.entity;

import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
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

    public static Order create(User user, Ticket ticket, String orderCode, TicketType ticketType) {
        OrderStatus initialStatus = (ticketType == TicketType.FIRST_COME)
            ? OrderStatus.COMPLETED
            : OrderStatus.PENDING;

        return Order.builder()
            .user(user)
            .ticket(ticket)
            .orderCode(orderCode)
            .status(initialStatus)
            .build();
    }

    /**
     * @deprecated Use {@link #assignQrCode(TicketQrCode)} instead
     */
    @Deprecated
	public void updateTicketQrCode(TicketQrCode ticketQrCode) {
		assignQrCode(ticketQrCode);
	}

    /**
     * @deprecated Use {@link #cancel()} instead
     */
    @Deprecated
	public void cancelOrder() {
		cancel();
	}

    /**
     * @deprecated Use {@link #approve()} instead
     */
    public void approveOrder() {
        approve();
    }

    public void validateOwner(User user) {
        if(!this.user.equals(user)) {
            throw new GeneralException(ErrorStatus._ORDER_NOT_MATCH_USER);
        }
    }

    public void assignQrCode(TicketQrCode ticketQrCode) {
        this.ticketQrCode = ticketQrCode;
        ticketQrCode.updateOrder(this);
    }

    public void approve(){
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(){
        this.status = OrderStatus.CANCELED;
    }
}