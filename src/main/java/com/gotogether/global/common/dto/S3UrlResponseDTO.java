package com.gotogether.global.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3UrlResponseDTO {
	private String preSignedUrl;
}


