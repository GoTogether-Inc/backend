package com.gotogether.domain.term.entity;

import java.time.LocalDateTime;

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
@Table(name = "terms")
public class
Term extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "is_service_agreed", nullable = false)
	private boolean isServiceAgreed;

	@Column(name = "is_privacy_policy_agreed", nullable = false)
	private boolean isPrivacyPolicyAgree;

	@Column(name = "is_personal_info_usage_agreed", nullable = false)
	private boolean isPersonalInfoUsageAgreed;

	@Column(name = "is_marketing_agreed", nullable = false)
	private boolean isMarketingAgreed;

	@Column(name = "agreed_at", nullable = false)
	private LocalDateTime agreedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Term(boolean isServiceAgreed, boolean isPrivacyPolicyAgree, boolean isPersonalInfoUsageAgreed,
		boolean isMarketingAgreed, LocalDateTime agreedAt, User user) {
		this.isServiceAgreed = isServiceAgreed;
		this.isPrivacyPolicyAgree = isPrivacyPolicyAgree;
		this.isPersonalInfoUsageAgreed = isPersonalInfoUsageAgreed;
		this.isMarketingAgreed = isMarketingAgreed;
		this.agreedAt = agreedAt;
		this.user = user;
	}
}