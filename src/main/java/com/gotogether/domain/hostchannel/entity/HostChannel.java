package com.gotogether.domain.hostchannel.entity;

import java.util.List;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "host_channels")
public class HostChannel extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private HostChannelStatus status;

	@OneToMany(mappedBy = "hostChannel", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Event> events;

	@OneToMany(mappedBy = "hostChannel", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChannelOrganizer> channelOrganizers;

	@Builder
	public HostChannel(String name, String email, String description, String profileImageUrl) {
		this.name = name;
		this.email = email;
		this.description = description;
		this.profileImageUrl = profileImageUrl;
		this.status = HostChannelStatus.ACTIVE;
	}

	public void updateStatus(HostChannelStatus status) {
		this.status = status;
	}

	public void update(HostChannelRequestDTO request) {
		this.name = request.getHostChannelName();
		this.email = request.getHostEmail();
		this.description = request.getChannelDescription();
		this.profileImageUrl = request.getProfileImageUrl();
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
}
