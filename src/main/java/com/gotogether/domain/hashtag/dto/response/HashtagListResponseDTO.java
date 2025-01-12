package com.gotogether.domain.hashtag.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HashtagListResponseDTO {
	private Long id;
	private String hashtagName;
}