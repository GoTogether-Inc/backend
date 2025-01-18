package com.gotogether.domain.card.entity;

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
@Table(name = "card")
public class Card extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "number", nullable = false)
	private String number;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "billing_key", nullable = false)
	private String billingKey;

	@Column(name = "expiration_date", nullable = false)
	private String expirationDate;

	@Column(name = "owner", nullable = false)
	private String owner;

	@Column(name = "is_default", nullable = false, columnDefinition = "bit(1) default 0")
	private boolean isDefault;

	@Column(name = "status", nullable = false)
	private CardStatus status;

	@Builder
	public Card(User user, String name, String code, String number, String password, String billingKey,
		String expirationDate, String owner, boolean isDefault) {
		this.user = user;
		this.name = name;
		this.code = code;
		this.number = number;
		this.password = password;
		this.billingKey = billingKey;
		this.expirationDate = expirationDate;
		this.owner = owner;
		this.isDefault = isDefault;
		this.status = CardStatus.ACTIVE;
	}

	public void updateStatus(CardStatus status) {
		this.status = status;
	}
}

