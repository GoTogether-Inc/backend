package com.gotogether.domain.hostchannel.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
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