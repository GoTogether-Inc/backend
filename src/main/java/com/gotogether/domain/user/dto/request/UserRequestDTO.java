package com.gotogether.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDTO {

	@NotBlank(message = "이름은 필수입니다.")
	private String name;

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다.")
	private String phoneNumber;
}