package com.gotogether.domain.event;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
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
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EventService eventService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;

	private Event event;
	private EventRequestDTO eventRequestDTO;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		HostChannel hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("testchannel@example.com")
			.description("This is a test channel.")
			.profileImageUrl("http://example.com/image.png")
			.build();

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

		List<ReferenceLinkDTO> referenceLinks = Arrays.asList(
			ReferenceLinkDTO.builder()
				.title("Test Link")
				.url("https://test.com")
				.build()
		);

		eventRequestDTO = EventRequestDTO.builder()
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
			.hostChannelId(1L)
			.hashtags(Arrays.asList("test", "event"))
			.referenceLinks(referenceLinks)
			.build();
	}

	@Test
	@DisplayName("이벤트 생성")
	void createEvent() throws Exception {
		// GIVEN
		given(eventService.createEvent(any(EventRequestDTO.class))).willReturn(event);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result").value(1L))
			.andDo(print());

		verify(eventService).createEvent(argThat(req ->
			req.getTitle().equals(eventRequestDTO.getTitle()) &&
				req.getHostChannelId().equals(eventRequestDTO.getHostChannelId()) &&
				req.getCategory().equals(eventRequestDTO.getCategory()) &&
				req.getOnlineType().equals(eventRequestDTO.getOnlineType())
		));
	}

	@Test
	@DisplayName("이벤트 상세 조회")
	void getDetailEvent() throws Exception {
		// GIVEN
		Long eventId = 1L;
		EventDetailResponseDTO response = EventDetailResponseDTO.builder()
			.id(eventId)
			.bannerImageUrl("https://example.com/banner.jpg")
			.title("Test Event")
			.participantCount(100)
			.startDate(String.valueOf(LocalDate.now()))
			.endDate(String.valueOf(LocalDate.now().plusDays(1)))
			.address("Test Location")
			.locationLat(100.0)
			.locationLng(200.0)
			.description("This is a test event")
			.hostChannelName("Test Channel")
			.hostChannelDescription("This is a test channel")
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.referenceLinks(List.of(
				ReferenceLinkDTO.builder()
					.title("Test Link")
					.url("https://test.com")
					.build()
			))
			.build();

		given(eventService.getDetailEvent(null, eventId)).willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/{eventId}", eventId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.id").value(eventId))
			.andExpect(jsonPath("$.result.title").value("Test Event"))
			.andExpect(jsonPath("$.result.address").value("Test Location"))
			.andExpect(jsonPath("$.result.organizerEmail").value("test@example.com"))
			.andExpect(jsonPath("$.result.referenceLinks[0].title").value("Test Link"))
			.andExpect(jsonPath("$.result.referenceLinks[0].url").value("https://test.com"))
			.andDo(print());

		verify(eventService).getDetailEvent(null, eventId);
	}

	@Test
	@DisplayName("이벤트 수정")
	void updateEvent() throws Exception {
		// GIVEN
		given(eventService.updateEvent(eq(event.getId()), argThat(req ->
			req.getTitle().equals(eventRequestDTO.getTitle()) &&
				req.getHostChannelId().equals(eventRequestDTO.getHostChannelId()) &&
				req.getCategory().equals(eventRequestDTO.getCategory()) &&
				req.getOnlineType().equals(eventRequestDTO.getOnlineType())
		))).willReturn(event);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/events/{eventId}", event.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventRequestDTO))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value(event.getId()))
			.andDo(print());

		verify(eventService).updateEvent(eq(event.getId()), argThat(req ->
			req.getTitle().equals(eventRequestDTO.getTitle()) &&
				req.getHostChannelId().equals(eventRequestDTO.getHostChannelId()) &&
				req.getCategory().equals(eventRequestDTO.getCategory()) &&
				req.getOnlineType().equals(eventRequestDTO.getOnlineType())
		));
	}

	@Test
	@DisplayName("이벤트 삭제")
	void deleteEvent() throws Exception {
		// GIVEN
		willDoNothing().given(eventService).deleteEvent(event.getId());

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/events/{eventId}", event.getId())
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("이벤트 삭제 성공"))
			.andDo(print());

		verify(eventService).deleteEvent(event.getId());
	}

	@Test
	@DisplayName("이벤트 목록 조회")
	void getEvents() throws Exception {
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

		given(eventService.getEventsByTag(tags, PageRequest.of(page, size))).willReturn(eventPage);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events")
				.param("tags", tags)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result[0].id").value(2))
			.andExpect(jsonPath("$.result[0].title").value("Event 2"))
			.andExpect(jsonPath("$.result[1].id").value(1))
			.andExpect(jsonPath("$.result[1].title").value("Event 1"))
			.andDo(print());

		verify(eventService).getEventsByTag(tags, PageRequest.of(page, size));
	}

	@Test
	@DisplayName("이벤트 검색")
	void getEventsSearch() throws Exception {
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

		given(eventService.searchEvents(keyword, PageRequest.of(page, size))).willReturn(eventPage);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/search")
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result[0].title").value(org.hamcrest.Matchers.containsString(keyword)))
			.andExpect(jsonPath("$.result[1].title").value(org.hamcrest.Matchers.containsString(keyword)))
			.andDo(print());

		verify(eventService).searchEvents(keyword, PageRequest.of(page, size));
	}

	@Test
	@DisplayName("카테고리별 이벤트 조회")
	void getEventsByCategory() throws Exception {
		// GIVEN
		EventListResponseDTO response = EventListResponseDTO.builder()
			.id(1L)
			.bannerImageUrl("https://example.com/banner.jpg")
			.title("Test Event")
			.hostChannelName("Test Channel")
			.startDate(String.valueOf(LocalDateTime.now()))
			.address("Test Address")
			.onlineType("ONLINE")
			.hashtags(List.of("DEVELOP", "CONFERENCE"))
			.remainDays("D-5")
			.build();

		Page<EventListResponseDTO> page = new PageImpl<>(List.of(response));

		given(eventService.getEventsByCategory(eq(Category.DEVELOPMENT_STUDY), any(PageRequest.class)))
			.willReturn(page);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/categories")
				.param("category", "DEVELOPMENT_STUDY")
				.param("page", "0")
				.param("size", "10")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].title").value("Test Event"))
			.andExpect(jsonPath("$.result[0].hostChannelName").value("Test Channel"))
			.andExpect(jsonPath("$.result[0].onlineType").value("ONLINE"))
			.andDo(print());

		verify(eventService).getEventsByCategory(eq(Category.DEVELOPMENT_STUDY), any(PageRequest.class));
	}
}