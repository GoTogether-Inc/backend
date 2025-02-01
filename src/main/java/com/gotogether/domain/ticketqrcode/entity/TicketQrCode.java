package com.gotogether.domain.ticketqrcode.entity;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@Column(name = "qr_code_image_url", nullable = false)
	private String qrCodeImageUrl;

	@Column(name = "is_used", nullable = false, columnDefinition = "bit(1) default 0")
	private boolean isUsed;

	@Builder
	public TicketQrCode(Order order, String qrCodeImageUrl, boolean isUsed) {
		this.order = order;
		this.qrCodeImageUrl = qrCodeImageUrl;
		this.isUsed = isUsed;
	}
}
