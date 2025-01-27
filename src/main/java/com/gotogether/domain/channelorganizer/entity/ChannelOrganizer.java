package com.gotogether.domain.channelorganizer.entity;

import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.common.entity.BaseEntity;

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
@Table(name = "channel_organizer")
public class ChannelOrganizer extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_channel_id", nullable = false)
	private HostChannel hostChannel;

	@Builder
	public ChannelOrganizer(User user, HostChannel hostChannel) {
		this.user = user;
		this.hostChannel = hostChannel;
	}
}
