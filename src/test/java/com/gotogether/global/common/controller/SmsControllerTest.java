package com.gotogether.global.common.controller;

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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.global.common.dto.SmsRequestDTO;
import com.gotogether.global.common.dto.SmsVerifyRequestDTO;
import com.gotogether.global.common.service.SmsService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class SmsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SmsService smsService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private SmsRequestDTO smsRequestDTO;
	private SmsVerifyRequestDTO smsVerifyRequestDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		smsRequestDTO = SmsRequestDTO.builder()
			.phoneNumber("010-1234-5678")
			.build();

		smsVerifyRequestDTO = SmsVerifyRequestDTO.builder()
			.phoneNumber("010-1234-5678")
			.certificationCode("123456")
			.build();
	}

	@Test
	@DisplayName("SMS 인증번호 발송")
	void sendSms() throws Exception {
		// GIVEN
		willDoNothing().given(smsService).sendCertificationCode(any(SmsRequestDTO.class));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/sms/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(smsRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("인증번호 발송 성공"))
			.andDo(print());

		verify(smsService).sendCertificationCode(argThat(req ->
			req.getPhoneNumber().equals(smsRequestDTO.getPhoneNumber())
		));
	}

	@Test
	@DisplayName("SMS 인증번호 검증")
	void verifySms() throws Exception {
		// GIVEN
		willDoNothing().given(smsService).verifyCertificationCode(any(SmsVerifyRequestDTO.class));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/sms/verify")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(smsVerifyRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("전화번호 인증 성공"))
			.andDo(print());

		verify(smsService).verifyCertificationCode(argThat(req ->
			req.getPhoneNumber().equals(smsVerifyRequestDTO.getPhoneNumber()) &&
				req.getCertificationCode().equals(smsVerifyRequestDTO.getCertificationCode())
		));
	}
} 