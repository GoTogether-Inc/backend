package com.gotogether.domain.user.entity;

import java.util.List;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.card.entity.Card;
import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "phone_number", unique = true)
	private String phoneNumber;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "provider", nullable = false)
	private String provider;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	@Column(name = "status", nullable = false)
	private UserStatus status;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Card> cards;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Bookmark> bookmarks;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChannelOrganizer> channelOrganizers;

	@Builder
	public User(String name, String phoneNumber, String provider, String providerId, String email) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.provider = provider;
		this.providerId = providerId;
		this.status = UserStatus.ACTIVE;
	}

	public void updateStatus(UserStatus status) {
		this.status = status;
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updatePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}