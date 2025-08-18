package com.gotogether.domain.ticket;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.hostchannel.entity.HostChannel;
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
	private Event event;
	private Ticket ticket;
	private TicketRequestDTO ticketRequestDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		HostChannel hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("testchannel@example.com")
			.description("This is a test channel.")
			.profileImageUrl("http://example.com/image.png")
			.build();
		ReflectionTestUtils.setField(hostChannel, "id", 1L);

		event = Event.builder()
			.title("Test Event")
			.description("Test Description")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(7))
			.bannerImageUrl("http://example.com/banner.jpg")
			.address("Test Address")
			.detailAddress("Test Detail Address")
			.locationLat(37.5665)
			.locationLng(126.9780)
			.onlineType(OnlineType.OFFLINE)
			.category(Category.DEVELOPMENT_STUDY)
			.organizerEmail("organizer@example.com")
			.organizerPhoneNumber("010-9876-5432")
			.hostChannel(hostChannel)
			.build();
		ReflectionTestUtils.setField(event, "id", 1L);

		ticket = Ticket.builder()
			.event(event)
			.name("Test Ticket")
			.description("This is a test ticket.")
			.price(50000)
			.availableQuantity(100)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.build();
		ReflectionTestUtils.setField(ticket, "id", 1L);

		ticketRequestDTO = TicketRequestDTO.builder()
			.eventId(1L)
			.ticketType(TicketType.FIRST_COME)
			.ticketName("Test Ticket")
			.ticketDescription("This is a test ticket.")
			.ticketPrice(50000)
			.availableQuantity(100)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.build();
	}

	@Test
	@DisplayName("티켓 생성")
	void createTicket() throws Exception {
		// GIVEN
		given(ticketService.createTicket(any(TicketRequestDTO.class))).willReturn(ticket);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/tickets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticketRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result").value(1L))
			.andDo(print());

		verify(ticketService).createTicket(argThat(req ->
			req.getEventId().equals(ticketRequestDTO.getEventId()) &&
				req.getTicketType().equals(ticketRequestDTO.getTicketType()) &&
				req.getTicketName().equals(ticketRequestDTO.getTicketName()) &&
				req.getTicketPrice() == ticketRequestDTO.getTicketPrice()
		));
	}

	@Test
	@DisplayName("티켓 목록 조회")
	void getTickets() throws Exception {
		// GIVEN
		Long eventId = 1L;
		
		List<TicketListResponseDTO> ticketList = Arrays.asList(
			TicketListResponseDTO.builder()
				.ticketId(1L)
				.ticketName("Test Ticket")
				.ticketDescription("This is a test ticket.")
				.ticketPrice(30000)
				.availableQuantity(50)
				.startDate(LocalDateTime.now())
				.endDate(LocalDateTime.now().plusDays(1))
				.build(),
			TicketListResponseDTO.builder()
				.ticketId(2L)
				.ticketName("Test Ticket2")
				.ticketDescription("This is a test ticket2.")
				.ticketPrice(50000)
				.availableQuantity(100)
				.startDate(LocalDateTime.now())
				.endDate(LocalDateTime.now().plusDays(1))
				.build()
		);

		given(ticketService.getTickets(eventId)).willReturn(ticketList);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/tickets")
				.param("eventId", String.valueOf(eventId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].ticketId").value(1L))
			.andExpect(jsonPath("$.result[0].ticketName").value("Test Ticket"))
			.andExpect(jsonPath("$.result[0].ticketPrice").value(30000))
			.andExpect(jsonPath("$.result[1].ticketId").value(2L))
			.andExpect(jsonPath("$.result[1].ticketName").value("Test Ticket2"))
			.andExpect(jsonPath("$.result[1].ticketPrice").value(50000))
			.andDo(print());

		verify(ticketService).getTickets(eventId);
	}

	@Test
	@DisplayName("티켓 삭제")
	void deleteTicket() throws Exception {
		// GIVEN
		Long ticketId = 1L;
		
		willDoNothing().given(ticketService).deleteTicket(ticketId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/tickets/{ticketId}", ticketId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("티켓 삭제 성공"))
			.andDo(print());

		verify(ticketService).deleteTicket(ticketId);
	}
}
