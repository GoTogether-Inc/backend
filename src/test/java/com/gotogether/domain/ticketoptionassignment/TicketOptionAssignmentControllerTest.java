package com.gotogether.domain.ticketoptionassignment;

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

import com.gotogether.domain.ticketoptionassignment.service.TicketOptionAssignmentService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class TicketOptionAssignmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TicketOptionAssignmentService ticketOptionAssignmentService;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();
	}

	@Test
	@DisplayName("티켓 옵션 할당")
	void assignTicketOption() throws Exception {
		// GIVEN
		Long ticketId = 1L;
		Long ticketOptionId = 1L;

		willDoNothing().given(ticketOptionAssignmentService).assignTicketOption(ticketId, ticketOptionId);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/ticket-option-assignments")
				.param("ticketId", String.valueOf(ticketId))
				.param("ticketOptionId", String.valueOf(ticketOptionId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 옵션 부착 완료"))
			.andDo(print());

		verify(ticketOptionAssignmentService).assignTicketOption(ticketId, ticketOptionId);
	}

	@Test
	@DisplayName("티켓 옵션 할당 해제")
	void unassignTicketOption() throws Exception {
		// GIVEN
		Long ticketId = 1L;
		Long ticketOptionId = 1L;

		willDoNothing().given(ticketOptionAssignmentService).unassignTicketOption(ticketId, ticketOptionId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/ticket-option-assignments")
				.param("ticketId", String.valueOf(ticketId))
				.param("ticketOptionId", String.valueOf(ticketOptionId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 옵션 부착 취소 완료"))
			.andDo(print());

		verify(ticketOptionAssignmentService).unassignTicketOption(ticketId, ticketOptionId);
	}
} 