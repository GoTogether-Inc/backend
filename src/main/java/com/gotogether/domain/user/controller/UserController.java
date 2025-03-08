package com.gotogether.domain.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.service.UserService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/sign-up")
	public ApiResponse<?> signUp(@RequestBody UserRequestDTO request) {
		User user = userService.createUser(request);
		return ApiResponse.onSuccessCreated(user.getId());
	}
}