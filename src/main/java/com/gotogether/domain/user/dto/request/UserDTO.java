package com.gotogether.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
	private Long id;
	private String name;
	private String email;
	private String provider;
	private String providerId;
}