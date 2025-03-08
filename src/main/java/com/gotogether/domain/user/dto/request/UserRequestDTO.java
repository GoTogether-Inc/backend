package com.gotogether.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDTO {
	private Long id;
	private String name;
	private String phoneNumber;
	private String email;
}