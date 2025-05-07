package com.gotogether.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.service.UserService;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@PutMapping
	public ApiResponse<?> updateNameAndPhoneNumber(
		@AuthUser Long userId,
		@RequestBody @Valid UserRequestDTO request) {
		User user = userService.updateNameAndPhoneNumber(userId, request);
		return ApiResponse.onSuccess(user.getId());
	}

	@GetMapping
	public ApiResponse<UserDetailResponseDTO> getDetailUser(
		@AuthUser Long userId) {
		UserDetailResponseDTO response = userService.getDetailUser(userId);
		return ApiResponse.onSuccess(response);
	}
}