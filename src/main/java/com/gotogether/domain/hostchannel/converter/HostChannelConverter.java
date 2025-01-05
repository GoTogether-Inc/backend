package com.gotogether.domain.hostchannel.converter;

import java.util.List;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;

public class HostChannelConverter {

	public static HostChannel toEntity(HostChannelRequestDTO request) {
		return HostChannel.builder()
			.profileImageUrl(request.getProfileImageUrl())
			.name(request.getHostChannelName())
			.email(request.getHostEmail())
			.description(request.getChannelDescription())
			.build();
	}

	public static HostChannelListResponseDTO toHostChannelListResponseDTO(
		HostChannel hostChannel) {
		return HostChannelListResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.build();
	}

	public static HostChannelDetailResponseDTO toHostChannelDetailResponseDTO(
		HostChannel hostChannel) {

		List<EventListResponseDTO> eventListResponseDTOList = hostChannel.getEvents().stream()
			.map(event -> EventListResponseDTO.builder()
				.id(event.getId())
				.bannerImageUrl(event.getBannerImageUrl())
				.title(event.getTitle())
				.hostChannelName(hostChannel.getName())
				.startDate(event.getStartDate().toString())
				.location(event.getLocation())
				.hashtags(
					event.getHashtags().stream()
						.map(Hashtag::getName)
						.toList()
				)
				.build()
			)
			.toList();

		return HostChannelDetailResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.channelDescription(hostChannel.getDescription())
			.events(eventListResponseDTOList)
			.build();
	}

	public static HostChannelMemberResponseDTO toHostChannelMemberResponseDTO(ChannelOrganizer channelOrganizer) {
		return HostChannelMemberResponseDTO.builder()
			.id(channelOrganizer.getUser().getId())
			.memberName(channelOrganizer.getUser().getName())
			.build();
	}
}
