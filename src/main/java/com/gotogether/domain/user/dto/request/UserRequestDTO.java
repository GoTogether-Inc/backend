package com.gotogether.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDTO {
	
	@NotNull(message = "사용자 ID는 필수입니다.")
	private Long id;

	@NotBlank(message = "이름은 필수입니다.")
	private String name;

	@NotBlank(message = "전화번호는 필수입니다.")
	private String phoneNumber;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	private String email;
}