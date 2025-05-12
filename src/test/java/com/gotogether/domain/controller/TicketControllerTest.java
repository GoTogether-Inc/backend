package com.gotogether.domain.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

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
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticket.service.TicketService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TicketService ticketService;

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
	@DisplayName("티켓 생성 성공")
	void testCreateTicket_onSuccess() throws Exception {
		// GIVEN
		TicketRequestDTO request = TicketRequestDTO.builder()
			.eventId(null)
			.ticketName("Test Ticket")
			.ticketDescription("This is a test ticket.")
			.ticketPrice(10000)
			.availableQuantity(50)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.startTime("10:00")
			.endTime("12:00")
			.ticketType(TicketType.FIRST_COME)
			.eventId(1L)
			.build();

		Ticket mockTicket = Ticket.builder()
			.event(null)
			.name("Test Ticket")
			.description("This is a test ticket.")
			.price(10000)
			.availableQuantity(50)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.build();

		ReflectionTestUtils.setField(mockTicket, "id", 1L);

		given(ticketService.createTicket(any(TicketRequestDTO.class))).willReturn(mockTicket);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/tickets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andDo(print());

		verify(ticketService).createTicket(refEq(request));
	}

	@Test
	@DisplayName("티켓 목록 조회 성공")
	void testGetTickets_onSuccess() throws Exception {
		// GIVEN
		TicketListResponseDTO response = TicketListResponseDTO.builder()
			.ticketId(1L)
			.ticketName("Test Ticket")
			.ticketDescription("This is a test ticket.")
			.ticketPrice(10000)
			.availableQuantity(50)
			.build();

		given(ticketService.getTickets(1L))
			.willReturn(Collections.singletonList(response));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/tickets")
				.param("eventId", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].ticketName").value("Test Ticket"))
			.andExpect(jsonPath("$.result[0].ticketDescription").value("This is a test ticket."))
			.andExpect(jsonPath("$.result[0].ticketPrice").value(10000))
			.andExpect(jsonPath("$.result[0].availableQuantity").value(50))
			.andDo(print());

		verify(ticketService).getTickets(1L);
	}

	@Test
	@DisplayName("티켓 삭제 성공")
	void testDeleteTicket_onSuccess() throws Exception {
		// GIVEN
		willDoNothing().given(ticketService).deleteTicket(1L);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/tickets/{ticketId}", 1L)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result").value("티켓 삭제 성공"))
			.andDo(print());

		verify(ticketService).deleteTicket(1L);
	}
}
