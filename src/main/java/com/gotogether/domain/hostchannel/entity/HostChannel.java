package com.gotogether.domain.hostchannel.entity;

import java.util.List;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
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
@Table(name = "host_channel")
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

	@Column(name = "profile_image_url", nullable = false)
	private String profileImageUrl;

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
		if (request.getHostChannelName() != null) {
			this.name = request.getHostChannelName();
		}
		if (request.getHostEmail() != null) {
			this.email = request.getHostEmail();
		}
		if (request.getChannelDescription() != null) {
			this.description = request.getChannelDescription();
		}
		if (request.getProfileImageUrl() != null) {
			this.profileImageUrl = request.getProfileImageUrl();
		}
	}
}
