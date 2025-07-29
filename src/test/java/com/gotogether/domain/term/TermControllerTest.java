package com.gotogether.domain.term;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.term.dto.request.TermRequestDTO;
import com.gotogether.domain.term.entity.Term;
import com.gotogether.domain.term.service.TermService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class TermControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TermService termService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private Term term;
	private TermRequestDTO termRequestDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		term = Term.builder()
			.isServiceAgreed(true)
			.isPrivacyPolicyAgree(true)
			.isPersonalInfoUsageAgreed(true)
			.isMarketingAgreed(false)
			.agreedAt(LocalDateTime.now())
			.user(testUser.user())
			.build();
		ReflectionTestUtils.setField(term, "id", 1L);

		termRequestDTO = TermRequestDTO.builder()
			.serviceAgreed(true)
			.privacyPolicyAgree(true)
			.personalInfoUsageAgreed(true)
			.marketingAgreed(false)
			.build();
	}

	@Test
	@DisplayName("이용약관 동의")
	void createTerm() throws Exception {
		// GIVEN
		given(termService.createTerm(any(Long.class), any(TermRequestDTO.class))).willReturn(term);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/terms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(termRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result").value(1L))
			.andDo(print());

		verify(termService).createTerm(any(Long.class), argThat(req ->
			req.isServiceAgreed() == termRequestDTO.isServiceAgreed() &&
				req.isPrivacyPolicyAgree() == termRequestDTO.isPrivacyPolicyAgree() &&
				req.isPersonalInfoUsageAgreed() == termRequestDTO.isPersonalInfoUsageAgreed() &&
				req.isMarketingAgreed() == termRequestDTO.isMarketingAgreed()
		));
	}
} 