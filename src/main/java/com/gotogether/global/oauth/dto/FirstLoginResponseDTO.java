package com.gotogether.global.oauth.dto;

import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FirstLoginResponseDTO {
	private UserDetailResponseDTO user;
	private String redirect;
}