package com.gotogether.domain.hostchannel.converter;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
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

	public static HostChannelListResponseDTO toHostChannelListResponseDTO(
		HostChannel hostChannel) {
		return HostChannelListResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.name(hostChannel.getName())
			.build();
	}

	public static HostChannelMemberResponseDTO toHostChannelMemberResponseDTO(ChannelOrganizer channelOrganizer) {
		return HostChannelMemberResponseDTO.builder()
			.id(channelOrganizer.getUser().getId())
			.name(channelOrganizer.getUser().getName())
			.build();
	}
}
