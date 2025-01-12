package com.gotogether.domain.hashtag.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagRequestDTO {
	@JsonProperty("hashtagName")
	private String hashtagName;
}