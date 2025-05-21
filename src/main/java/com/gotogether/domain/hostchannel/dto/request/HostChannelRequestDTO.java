package com.gotogether.domain.hostchannel.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostChannelRequestDTO {

	private String profileImageUrl;

	@NotBlank(message = "호스트 채널명은 필수입니다.")
	private String hostChannelName;

	@NotBlank(message = "호스트 이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	private String hostEmail;

	@NotBlank(message = "채널 설명은 필수입니다.")
	private String channelDescription;
}