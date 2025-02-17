package com.gotogether.domain.hostchannel.dto.response;

import java.util.List;

import com.gotogether.domain.event.dto.response.EventListResponseDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostChannelDetailResponseDTO {
	private Long id;
	private String profileImageUrl;
	private String hostChannelName;
	private String channelDescription;
	private List<EventListResponseDTO> events;
}
