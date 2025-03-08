package com.gotogether.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponseDTO {
	private Long id;
	private String name;
	private String phoneNumber;
	private String email;
}
