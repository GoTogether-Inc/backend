package com.gotogether.domain.hostchannel.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostChannelRequestDTO {
	@JsonProperty("profileImageUrl")
	private String profileImageUrl;

	@JsonProperty("hostChannelName")
	private String hostChannelName;

	@JsonProperty("hostEmail")
	private String hostEmail;

	@JsonProperty("channelDescription")
	private String channelDescription;
}