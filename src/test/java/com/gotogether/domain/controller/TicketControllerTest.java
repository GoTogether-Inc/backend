package com.gotogether.domain.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.ticket.dto.request.TicketRequestDTO;
import com.gotogether.domain.ticket.dto.response.TicketListResponseDTO;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticket.service.TicketService;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TicketService ticketService;

	@Autowired
	private ObjectMapper objectMapper;

	private Ticket ticket;

	@BeforeEach
	void setUp() {
		ticket = Ticket.builder()
			.name("Test Ticket")
			.description("This is a test ticket.")
			.price(10000)
			.availableQuantity(50)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.status(TicketStatus.AVAILABLE)
			.type(TicketType.FIRST_COME)
			.build();
	}

	@Test
	void testCreateTicket_onSuccess() throws Exception {
		// GIVEN
		TicketRequestDTO ticketRequestDTO = TicketRequestDTO.builder()
			.ticketName("Test Ticket")
			.ticketDescription("This is a test ticket.")
			.ticketPrice(10000)
			.availableQuantity(50)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.startTime(String.valueOf(LocalDateTime.now().getHour() + LocalDateTime.now().getMinute()))
			.endTime(String.valueOf(LocalDateTime.now().plusDays(1).getHour() + LocalDateTime.now().plusDays(1).getMinute()))
			.build();

		when(ticketService.createTicket(any(TicketRequestDTO.class))).thenReturn(ticket);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/tickets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticket)))
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"));

		verify(ticketService, times(1)).createTicket(any(TicketRequestDTO.class));
	}

	@Test
	void testGetTickets_onSuccess() throws Exception {
		// GIVEN
		TicketListResponseDTO ticketResponse = TicketListResponseDTO.builder()
			.ticketId(1L)
			.ticketName("Test Ticket")
			.ticketDescription("This is a test ticket.")
			.ticketPrice(10000)
			.availableQuantity(50)
			.build();

		when(ticketService.getTickets(1L)).thenReturn(Collections.singletonList(ticketResponse));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/tickets")
				.param("eventId", "1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].ticketName").value("Test Ticket"))
			.andExpect(jsonPath("$.result[0].ticketDescription").value("This is a test ticket."))
			.andExpect(jsonPath("$.result[0].ticketPrice").value(10000))
			.andExpect(jsonPath("$.result[0].availableQuantity").value(50));

		verify(ticketService, times(1)).getTickets(1L);
	}

	@Test
	void testDeleteTicket_onSuccess() throws Exception {
		// GIVEN
		doNothing().when(ticketService).deleteTicket(1L);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/tickets/{ticketId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result").value("티켓 삭제 성공"));

		verify(ticketService, times(1)).deleteTicket(1L);
	}
}
