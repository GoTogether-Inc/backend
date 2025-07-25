package com.gotogether.domain.user.service;

import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;

public interface UserService {
	User updateNameAndPhoneNumber(Long userId, UserRequestDTO request);

	UserDetailResponseDTO getDetailUser(Long userId);
}