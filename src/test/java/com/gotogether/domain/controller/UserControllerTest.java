package com.gotogether.domain.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.user.dto.request.UserRequestDTO;
import com.gotogether.domain.user.dto.response.UserDetailResponseDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.service.UserService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();
	}

	@Test
	@DisplayName("사용자 이름 및 전화번호 수정 성공")
	void testUpdateNameAndPhoneNumber() throws Exception {
		// GIVEN
		UserRequestDTO request = UserRequestDTO.builder()
			.name("Updated User")
			.phoneNumber("010-5678-1234")
			.build();

		User updatedUser = User.builder()
			.name("Updated User")
			.email(testUser.user().getEmail())
			.phoneNumber("010-5678-1234")
			.provider(testUser.user().getProvider())
			.providerId(testUser.user().getProviderId())
			.build();

		ReflectionTestUtils.setField(updatedUser, "id", testUser.user().getId());

		given(userService.updateNameAndPhoneNumber(eq(testUser.user().getId()), any(UserRequestDTO.class)))
			.willReturn(updatedUser);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result").value(testUser.user().getId()))
			.andDo(print());

		verify(userService).updateNameAndPhoneNumber(eq(testUser.user().getId()), any(UserRequestDTO.class));
	}

	@Test
	@DisplayName("사용자 상세 조회 성공")
	void testGetDetailUser_success() throws Exception {
		// GIVEN
		UserDetailResponseDTO response = UserDetailResponseDTO.builder()
			.id(testUser.user().getId())
			.name(testUser.user().getName())
			.email(testUser.user().getEmail())
			.phoneNumber(testUser.user().getPhoneNumber())
			.build();

		given(userService.getDetailUser(eq(testUser.user().getId())))
			.willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/users")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.name").value(testUser.user().getName()))
			.andExpect(jsonPath("$.result.phoneNumber").value(testUser.user().getPhoneNumber()))
			.andDo(print());

		verify(userService).getDetailUser(eq(testUser.user().getId()));
	}
}
