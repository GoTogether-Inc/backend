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
import org.springframework.test.web.servlet.MockMvc;

import com.amazonaws.HttpMethod;
import com.gotogether.global.common.dto.S3UrlResponseDTO;
import com.gotogether.global.common.service.S3UploadService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class S3UploadControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private S3UploadService s3UploadService;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private S3UrlResponseDTO s3UrlResponseDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		s3UrlResponseDTO = S3UrlResponseDTO.builder()
			.preSignedUrl("https://test-bucket.s3.amazonaws.com/temp/1/uuid_conference-banner.webp?AWSAccessKeyId=test&Expires=1234567890&Signature=test")
			.build();
	}

	@Test
	@DisplayName("S3 Pre-signed URL 생성")
	void generatePresignedUrl() throws Exception {
		// GIVEN
		String fileName = "conference-banner.webp";

		given(s3UploadService.generatePreSignUrl(any(Long.class), eq(fileName), eq(HttpMethod.PUT)))
			.willReturn(s3UrlResponseDTO);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/generate-presigned-url")
				.param("fileName", fileName)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.preSignedUrl").value(s3UrlResponseDTO.getPreSignedUrl()))
			.andDo(print());

		verify(s3UploadService).generatePreSignUrl(any(Long.class), eq(fileName), eq(HttpMethod.PUT));
	}
} 