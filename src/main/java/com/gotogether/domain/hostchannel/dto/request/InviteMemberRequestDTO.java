package com.gotogether.domain.hostchannel.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMemberRequestDTO {

	@Email(message = "올바른 이메일 형식이어야 합니다.")
	private String email;
}
