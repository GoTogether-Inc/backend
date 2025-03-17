package com.gotogether.domain.hostchannel.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostChannelInfoResponseDTO {
	private Long id;
	private String profileImageUrl;
	private String hostChannelName;
	private String channelDescription;
	private String email;
	private List<HostChannelMemberResponseDTO> hostChannelMembers;
}