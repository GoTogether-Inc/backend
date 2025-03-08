package com.gotogether.domain.user.converter;

import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;

public class UserConverter {

	public static UserDetailResponseDTO toUserDetailResponseDTO(User user) {
		return UserDetailResponseDTO.builder()
			.id(user.getId())
			.name(user.getName())
			.phoneNumber(user.getPhoneNumber())
			.email(user.getEmail())
			.build();
	}
}
