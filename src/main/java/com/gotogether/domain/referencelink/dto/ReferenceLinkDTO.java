package com.gotogether.domain.referencelink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReferenceLinkDTO {

	@NotBlank(message = "참조 링크 제목은 필수입니다.")
	private String title;

	@NotBlank(message = "참조 링크 URL은 필수입니다.")
	private String url;
}
