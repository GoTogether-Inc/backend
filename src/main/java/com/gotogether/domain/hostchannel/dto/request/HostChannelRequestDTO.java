package com.gotogether.domain.hostchannel.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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