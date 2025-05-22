package com.gotogether.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.user.converter.UserConverter;
import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final EventFacade eventFacade;

	@Override
	@Transactional
	public User updateNameAndPhoneNumber(Long userId, UserRequestDTO request) {
		User user = eventFacade.getUserById(userId);

		if (isPhoneNumberDuplicate(request.getPhoneNumber(), userId)) {
			throw new GeneralException(ErrorStatus._USER_PHONE_NUMBER_DUPLICATE);
		}

		user.updateName(request.getName());
		user.updatePhoneNumber(request.getPhoneNumber());
		userRepository.save(user);

		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailResponseDTO getDetailUser(Long userId) {
		User user = eventFacade.getUserById(userId);

		return UserConverter.toUserDetailResponseDTO(user);
	}

	private boolean isPhoneNumberDuplicate(String phoneNumber, Long userId) {
		return userRepository.existsByPhoneNumberAndIdNot(phoneNumber, userId);
	}
}