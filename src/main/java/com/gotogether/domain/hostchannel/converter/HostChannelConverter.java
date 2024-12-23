package com.gotogether.domain.hostchannel.converter;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;

public class HostChannelConverter {

	public static HostChannel toEntity(HostChannelRequestDTO request) {
		return HostChannel.builder()
			.profileImageUrl(request.getProfileImageUrl())
			.name(request.getName())
			.email(request.getEmail())
			.description(request.getDescription())
			.build();
	}
}
