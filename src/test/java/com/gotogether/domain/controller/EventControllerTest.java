package com.gotogether.domain.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.service.EventService;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EventService eventService;

	@Autowired
	private ObjectMapper objectMapper;

	private HostChannel testHostChannel;

	@BeforeEach
	void setUp() {
		testHostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("testchannel@example.com")
			.description("This is a test channel.")
			.profileImageUrl("http://example.com/image.png")
			.build();
	}

	@Test
	void testCreateEvent_onSuccess() throws Exception {
		// GIVEN
		EventRequestDTO request = EventRequestDTO.builder()
			.hostChannelId(1L)
			.title("Test Event")
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.startTime(String.valueOf(LocalDateTime.now().toLocalTime()))
			.endTime(String.valueOf(LocalDateTime.now().plusHours(8).toLocalTime()))
			.description("This is a test event")
			.bannerImageUrl("http://example.com/banner.jpg")
			.address("Test Location")
			.location(Map.of("Lat", 100.0, "Lng", 200.0))
			.hashtags(List.of("test", "event"))
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Site")
					.url("http://test.com")
					.build()
			))
			.build();

		Event createdEvent = Event.builder()
			.title("Test Event")
			.description("This is a test channel")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.bannerImageUrl("http://example.com/banner.jpg")
			.address("Test Location")
			.locationLat(100.0)
			.locationLng(200.0)
			.onlineType(OnlineType.ONLINE)
			.category(Category.DEVELOPMENT_STUDY)
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.hostChannel(testHostChannel)
			.build();

		when(eventService.createEvent(any(EventRequestDTO.class))).thenReturn(createdEvent);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"));

		verify(eventService, times(1)).createEvent(any(EventRequestDTO.class));
	}

	@Test
		// 호스트 채널 없을 때
	void testCreateEvent_onFailure() throws Exception {
		// GIVEN
		EventRequestDTO request = EventRequestDTO.builder()
			.hostChannelId(5L)
			.title("Test Event")
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.startTime(String.valueOf(LocalDateTime.now().toLocalTime()))
			.endTime(String.valueOf(LocalDateTime.now().plusHours(8).toLocalTime()))
			.description("This is a test event")
			.bannerImageUrl("http://example.com/banner.jpg")
			.address("Test Location")
			.location(Map.of("Lat", 100.0, "Lng", 200.0))
			.hashtags(List.of("test", "event"))
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Site")
					.url("http://test.com")
					.build()
			))
			.build();

		when(eventService.createEvent(any(EventRequestDTO.class)))
			.thenThrow(new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("HOST_CHANNEL4001"))
			.andExpect(jsonPath("$.message").value("호스트 채널이 없습니다."));
	}

	@Test
	void testGetDetailEvent_onSuccess() throws Exception {
		// GIVEN
		Long eventId = 1L;
		EventDetailResponseDTO detailResponse = EventDetailResponseDTO.builder()
			.id(eventId)
			.bannerImageUrl("https://example.com/banner.jpg")
			.title("Test Event")
			.participantCount(100)
			.startDate(String.valueOf(LocalDate.now()))
			.endDate(String.valueOf(LocalDate.now().plusDays(1)))
			.address("Test Location")
			.location(Map.of("Lat", 100.0, "Lng", 200.0))
			.description("This is a test event")
			.hostChannelName("Test Channel")
			.hostChannelDescription("This is a test channel")
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Site")
					.url("http://test.com")
					.build()
			))
			.build();

		when(eventService.getDetailEvent(eventId)).thenReturn(detailResponse);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/{eventId}", eventId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.id").value(eventId))
			.andExpect(jsonPath("$.result.title").value("Test Event"))
			.andExpect(jsonPath("$.result.address").value("Test Location"))
			.andExpect(jsonPath("$.result.organizerEmail").value("test@example.com"))
			.andExpect(jsonPath("$.result.referenceLinks[0].title").value("Test Site"))
			.andExpect(jsonPath("$.result.referenceLinks[0].url").value("http://test.com"));
	}

	@Test
		// 이벤트 없을 때
	void testGetDetailEvent_onFailure() throws Exception {
		// GIVEN
		Long eventId = 5L;
		EventDetailResponseDTO detailResponse = EventDetailResponseDTO.builder()
			.id(eventId)
			.bannerImageUrl("https://example.com/banner.jpg")
			.title("Test Event")
			.participantCount(100)
			.startDate(String.valueOf(LocalDate.now()))
			.endDate(String.valueOf(LocalDate.now().plusDays(1)))
			.address("Test Location")
			.location(Map.of("Lat", 100.0, "Lng", 200.0))
			.description("This is a test event")
			.hostChannelName("Test Channel")
			.hostChannelDescription("This is a test channel")
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Site")
					.url("http://test.com")
					.build()
			))
			.build();

		when(eventService.getDetailEvent(eventId))
			.thenThrow(new GeneralException(ErrorStatus._EVENT_NOT_FOUND));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/{eventId}", eventId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("EVENT4001"))
			.andExpect(jsonPath("$.message").value("이벤트가 없습니다."));
	}

	@Test
	void testUpdateEvent_onSuccess() throws Exception {
		// GIVEN
		Long eventId = 1L;
		EventRequestDTO request = EventRequestDTO.builder()
			.hostChannelId(1L)
			.title("Updated Event")
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.startTime(String.valueOf(LocalDateTime.now().toLocalTime()))
			.endTime(String.valueOf(LocalDateTime.now().plusHours(8).toLocalTime()))
			.bannerImageUrl("https://example.com/updated-banner.jpg")
			.description("This is a test event")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Site")
					.url("http://test.com")
					.build()
			))
			.address("Test Location")
			.location(Map.of("Lat", 100.0, "Lng", 200.0))
			.onlineType(OnlineType.ONLINE)
			.category(Category.CONFERENCE)
			.hashtags(List.of("test", "event"))
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.build();

		Event updatedEvent = Event.builder()
			.title("Updated Event")
			.description("This is a test event")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.bannerImageUrl("https://example.com/updated-banner.jpg")
			.address("Updated Location")
			.locationLat(100.0)
			.locationLng(200.0)
			.onlineType(OnlineType.ONLINE)
			.category(Category.CONFERENCE)
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.build();

		when(eventService.updateEvent(eq(eventId), any(EventRequestDTO.class))).thenReturn(updatedEvent);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/events/{eventId}", eventId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"));

		verify(eventService, times(1)).updateEvent(eq(eventId), any(EventRequestDTO.class));
	}

	@Test
		// 이벤트 없을 때
	void testUpdateEvent_onFailure() throws Exception {
		// GIVEN
		Long eventId = 5L;
		EventRequestDTO request = EventRequestDTO.builder()
			.hostChannelId(1L)
			.title("Updated Event")
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.startTime(String.valueOf(LocalDateTime.now().toLocalTime()))
			.endTime(String.valueOf(LocalDateTime.now().plusHours(8).toLocalTime()))
			.bannerImageUrl("https://example.com/updated-banner.jpg")
			.description("This is a test event")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Site")
					.url("http://test.com")
					.build()
			))
			.address("Test Location")
			.location(Map.of("Lat", 100.0, "Lng", 200.0))
			.onlineType(OnlineType.ONLINE)
			.category(Category.CONFERENCE)
			.hashtags(List.of("test", "event"))
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.build();

		when(eventService.updateEvent(eq(eventId), any(EventRequestDTO.class)))
			.thenThrow(new GeneralException(ErrorStatus._EVENT_NOT_FOUND));

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/events/{eventId}", eventId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("EVENT4001"))
			.andExpect(jsonPath("$.message").value("이벤트가 없습니다."));
	}

	@Test
	void testDeleteEvent_onSuccess() throws Exception {
		// GIVEN
		Long eventId = 1L;
		doNothing().when(eventService).deleteEvent(eventId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/events/{eventId}", eventId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("이벤트 삭제 성공"));
	}

	@Test
		// 이벤트 없을 때
	void testDeleteEvent_onFailure() throws Exception {
		// GIVEN
		Long eventId = 5L;
		doThrow(new GeneralException(ErrorStatus._EVENT_NOT_FOUND))
			.when(eventService).deleteEvent(eventId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/events/{eventId}", eventId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("EVENT4001"))
			.andExpect(jsonPath("$.message").value("이벤트가 없습니다."));
	}

	@Test
	void testGetEvents_onSuccess() throws Exception {
		// GIVEN
		String tags = "current";
		int page = 0;
		int size = 10;

		EventListResponseDTO event1 = EventListResponseDTO.builder()
			.id(1L)
			.title("Event 1")
			.address("Location 1")
			.startDate("2024-12-27")
			.build();

		EventListResponseDTO event2 = EventListResponseDTO.builder()
			.id(2L)
			.title("Event 2")
			.address("Location 2")
			.startDate("2024-12-28")
			.build();

		List<EventListResponseDTO> events = List.of(event2, event1);

		Page<EventListResponseDTO> eventPage = new PageImpl<>(events);

		when(eventService.getEventsByTag(eq(tags), any(Pageable.class))).thenReturn(eventPage);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events")
				.param("tags", tags)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result[0].id").value(2)) // Event 2가 먼저 조회
			.andExpect(jsonPath("$.result[0].title").value("Event 2"))
			.andExpect(jsonPath("$.result[1].id").value(1)) // Event 1가 나중에 조회
			.andExpect(jsonPath("$.result[1].title").value("Event 1"));
	}

	@Test
	void testGetEventsSearch_onSuccess() throws Exception {
		// GIVEN
		String keyword = "Test";
		int page = 0;
		int size = 10;

		EventListResponseDTO event1 = EventListResponseDTO.builder()
			.id(1L)
			.title("Test Event 1")
			.address("Location 1")
			.startDate("2024-12-27")
			.build();

		EventListResponseDTO event2 = EventListResponseDTO.builder()
			.id(2L)
			.title("Test Event 2")
			.address("Location 2")
			.startDate("2024-12-28")
			.build();

		List<EventListResponseDTO> events = List.of(event1, event2);

		Page<EventListResponseDTO> eventPage = new PageImpl<>(events);

		when(eventService.searchEvents(eq(keyword), any(Pageable.class))).thenReturn(eventPage);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/search")
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result[0].title").value(org.hamcrest.Matchers.containsString(keyword)))
			.andExpect(jsonPath("$.result[1].title").value(org.hamcrest.Matchers.containsString(keyword)));
	}
}