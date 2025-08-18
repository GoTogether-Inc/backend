package com.gotogether.domain.reservationemail;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.entity.ReservationEmailTargetType;
import com.gotogether.domain.reservationemail.service.ReservationEmailService;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationEmailControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReservationEmailService reservationEmailService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private Event event;
	private Ticket ticket;
	private ReservationEmail reservationEmail;
	private ReservationEmailRequestDTO reservationEmailRequestDTO;

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
			.price(50000)
			.description("Test ticket description")
			.availableQuantity(100)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.build();
		ReflectionTestUtils.setField(ticket, "id", 1L);

		List<String> recipients = Arrays.asList("test1@example.com", "test2@example.com");
		reservationEmail = ReservationEmail.builder()
			.event(event)
			.targetType(ReservationEmailTargetType.ALL)
			.targetTicket(null)
			.recipients(recipients)
			.title("Test Reservation Email")
			.content("This is a test reservation email content.")
			.reservationDate(LocalDateTime.now().plusDays(1))
			.build();
		ReflectionTestUtils.setField(reservationEmail, "id", 1L);

		reservationEmailRequestDTO = ReservationEmailRequestDTO.builder()
			.eventId(1L)
			.targetType(ReservationEmailTargetType.ALL)
			.ticketId(null)
			.recipients(recipients)
			.title("Test Reservation Email")
			.content("This is a test reservation email content.")
			.reservationDate(LocalDateTime.now().plusDays(1))
			.build();
	}

	@Test
	@DisplayName("예약 이메일 생성")
	void createReservationEmail() throws Exception {
		// GIVEN
		given(reservationEmailService.createReservationEmail(any(ReservationEmailRequestDTO.class)))
			.willReturn(reservationEmail);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/reservation-emails")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reservationEmailRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result").value(1L))
			.andDo(print());

		verify(reservationEmailService).createReservationEmail(argThat(req ->
			req.getEventId().equals(reservationEmailRequestDTO.getEventId()) &&
				req.getTargetType().equals(reservationEmailRequestDTO.getTargetType()) &&
				req.getTitle().equals(reservationEmailRequestDTO.getTitle()) &&
				req.getContent().equals(reservationEmailRequestDTO.getContent())
		));
	}

	@Test
	@DisplayName("예약 이메일 목록 조회")
	void getReservationEmails() throws Exception {
		// GIVEN
		Long eventId = 1L;
		String status = "PENDING";

		List<ReservationEmailDetailResponseDTO> reservationEmailList = Arrays.asList(
			ReservationEmailDetailResponseDTO.builder()
				.id(1L)
				.targetName("All Participants")
				.recipients(Arrays.asList("test1@example.com", "test2@example.com"))
				.title("Event Reminder")
				.content("Don't forget about our upcoming event!")
				.reservationDate("2024-12-30T10:00:00")
				.build(),
			ReservationEmailDetailResponseDTO.builder()
				.id(2L)
				.targetName("VIP Ticket Holders")
				.recipients(Arrays.asList("vip1@example.com", "vip2@example.com"))
				.title("VIP Access Information")
				.content("Special instructions for VIP attendees.")
				.reservationDate("2024-12-29T15:00:00")
				.build()
		);

		given(reservationEmailService.getReservationEmails(eventId, status)).willReturn(reservationEmailList);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/reservation-emails")
				.param("eventId", String.valueOf(eventId))
				.param("status", status)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].targetName").value("All Participants"))
			.andExpect(jsonPath("$.result[0].title").value("Event Reminder"))
			.andExpect(jsonPath("$.result[1].id").value(2L))
			.andExpect(jsonPath("$.result[1].targetName").value("VIP Ticket Holders"))
			.andExpect(jsonPath("$.result[1].title").value("VIP Access Information"))
			.andDo(print());

		verify(reservationEmailService).getReservationEmails(eventId, status);
	}

	@Test
	@DisplayName("예약 이메일 수정")
	void updateReservationEmail() throws Exception {
		// GIVEN
		Long reservationEmailId = 1L;
		ReservationEmail updatedReservationEmail = ReservationEmail.builder()
			.event(event)
			.targetType(ReservationEmailTargetType.TICKET)
			.targetTicket(ticket)
			.recipients(List.of("updated@example.com"))
			.title("Updated Title")
			.content("Updated content")
			.reservationDate(LocalDateTime.now().plusDays(2))
			.build();
		ReflectionTestUtils.setField(updatedReservationEmail, "id", reservationEmailId);

		given(reservationEmailService.updateReservationEmail(any(Long.class), any(ReservationEmailRequestDTO.class)))
			.willReturn(updatedReservationEmail);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/reservation-emails/{reservationEmailId}", reservationEmailId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reservationEmailRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value(reservationEmailId))
			.andDo(print());

		verify(reservationEmailService).updateReservationEmail(any(Long.class), argThat(req ->
			req.getEventId().equals(reservationEmailRequestDTO.getEventId()) &&
				req.getTargetType().equals(reservationEmailRequestDTO.getTargetType()) &&
				req.getTitle().equals(reservationEmailRequestDTO.getTitle()) &&
				req.getContent().equals(reservationEmailRequestDTO.getContent())
		));
	}

	@Test
	@DisplayName("예약 이메일 삭제")
	void deleteReservationEmail() throws Exception {
		// GIVEN
		Long reservationEmailId = 1L;

		willDoNothing().given(reservationEmailService).deleteReservationEmail(reservationEmailId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/reservation-emails/{reservationEmailId}", reservationEmailId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("이벤트에 대한 예약 알림 삭제 성공"))
			.andDo(print());

		verify(reservationEmailService).deleteReservationEmail(reservationEmailId);
	}

	@Test
	@DisplayName("예약 이메일 목록 조회 - status 파라미터 없이")
	void getReservationEmailsWithoutStatus() throws Exception {
		// GIVEN
		Long eventId = 1L;

		List<ReservationEmailDetailResponseDTO> reservationEmailList = Collections.singletonList(
			ReservationEmailDetailResponseDTO.builder()
				.id(1L)
				.targetName("All Participants")
				.recipients(List.of("test1@example.com"))
				.title("General Notification")
				.content("General event notification")
				.reservationDate("2024-12-30T10:00:00")
				.build()
		);

		given(reservationEmailService.getReservationEmails(eventId, null)).willReturn(reservationEmailList);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/reservation-emails")
				.param("eventId", String.valueOf(eventId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].title").value("General Notification"))
			.andDo(print());

		verify(reservationEmailService).getReservationEmails(eventId, null);
	}
} 