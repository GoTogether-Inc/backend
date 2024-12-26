package com.gotogether.domain.referencelink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReferenceLinkDTO {

	@JsonProperty("title")
	private String title;

	@JsonProperty("url")
	private String url;
}
