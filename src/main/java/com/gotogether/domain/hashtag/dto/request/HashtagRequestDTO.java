package com.gotogether.domain.hashtag.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagRequestDTO {

	@NotBlank(message = "해시태그 이름은 필수입니다.")
	private String hashtagName;
}