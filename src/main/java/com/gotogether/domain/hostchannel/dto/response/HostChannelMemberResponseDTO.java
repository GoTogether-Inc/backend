package com.gotogether.domain.hostchannel.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostChannelMemberResponseDTO {
	private Long id;
	private String memberName;
}