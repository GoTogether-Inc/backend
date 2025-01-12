package com.gotogether.domain.hostchannel.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostChannelListResponseDTO {
	private Long id;
	private String profileImageUrl;
	private String hostChannelName;
}
