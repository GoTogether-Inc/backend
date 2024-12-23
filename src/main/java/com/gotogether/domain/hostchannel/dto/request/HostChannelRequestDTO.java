package com.gotogether.domain.hostchannel.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class HostChannelRequestDTO {
	@JsonProperty("profileImageUrl")
	private String profileImageUrl;

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("description")
	private String description;
}