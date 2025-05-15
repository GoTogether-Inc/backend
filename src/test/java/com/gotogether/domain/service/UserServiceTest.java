package com.gotogether.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.domain.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private EventFacade eventFacade;

	@Test
	@DisplayName("사용자 이름과 전화번호를 업데이트")
	void updateNameAndPhoneNumber_Success() {
		// GIVEN
		Long userId = 1L;
		String newName = "Updated User";
		String newPhoneNumber = "010-5678-1234";

		UserRequestDTO request = UserRequestDTO.builder()
			.name(newName)
			.phoneNumber(newPhoneNumber)
			.build();

		User user = User.builder()
			.name("test user")
			.phoneNumber("010-0000-0000")
			.email("test@example.com")
			.provider("google")
			.providerId("12345")
			.build();

		when(eventFacade.getUserById(userId)).thenReturn(user);
		when(userRepository.existsByPhoneNumber(newPhoneNumber)).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(user);

		// WHEN
		User updatedUser = userService.updateNameAndPhoneNumber(userId, request);

		// THEN
		assertThat(updatedUser.getName()).isEqualTo(newName);
		assertThat(updatedUser.getPhoneNumber()).isEqualTo(newPhoneNumber);
		verify(eventFacade).getUserById(userId);
		verify(userRepository).existsByPhoneNumber(newPhoneNumber);
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("사용자 상세 정보를 조회")
	void getDetailUser_Success() {
		// GIVEN
		Long userId = 1L;
		User user = User.builder()
			.name("test user")
			.phoneNumber("010-1234-5678")
			.email("test@example.com")
			.provider("google")
			.providerId("12345")
			.build();

		when(eventFacade.getUserById(userId)).thenReturn(user);

		// WHEN
		UserDetailResponseDTO response = userService.getDetailUser(userId);

		// THEN
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo(user.getName());
		assertThat(response.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
		verify(eventFacade).getUserById(userId);
	}
} 