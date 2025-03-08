package com.gotogether.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public User createUser(UserRequestDTO request) {

		User user = userRepository.findById(request.getId())
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		user.updateName(request.getName());
		user.updatePhoneNumber(request.getPhoneNumber());
		userRepository.save(user);

		return user;
	}
}