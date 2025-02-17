package com.gotogether.domain.ticketqrcode.entity;

import com.gotogether.domain.order.entity.Order;
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
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket_qr_codes")
public class TicketQrCode extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@Lob
	@Column(name = "qr_code_image_url", columnDefinition = "TEXT")
	private String qrCodeImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private TicketStatus status;

	@Builder
	public TicketQrCode(Order order, String qrCodeImageUrl, TicketStatus status) {
		this.order = order;
		this.qrCodeImageUrl = qrCodeImageUrl;
		this.status = status;
	}

	public void updateOrder(Order order) {
		this.order = order;
	}
}
