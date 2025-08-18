package com.gotogether.domain.bookmark;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.service.BookmarkService;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class BookmarkControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookmarkService bookmarkService;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private Event event;

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
	}

	@Test
	@DisplayName("북마크 생성")
	void createBookmark() throws Exception {
		// GIVEN
		Long eventId = 1L;

		Bookmark createdBookmark = Bookmark.builder()
			.user(testUser.user())
			.event(event)
			.build();
		ReflectionTestUtils.setField(createdBookmark, "id", 1L);

		given(bookmarkService.createBookmark(any(Long.class), any(Long.class))).willReturn(createdBookmark);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/events/{eventId}/bookmark", eventId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result").value(1L))
			.andDo(print());

		verify(bookmarkService).createBookmark(any(Long.class), any(Long.class));
	}

	@Test
	@DisplayName("사용자 북마크 목록 조회")
	void getUserBookmarks() throws Exception {
		// GIVEN
		Long eventId = 1L;

		List<EventListResponseDTO> bookmarkList = Arrays.asList(
			EventListResponseDTO.builder()
				.id(1L)
				.title("Test Event 1")
				.bannerImageUrl("http://example.com/banner1.jpg")
				.hostChannelName("Test Channel 1")
				.startDate("2024-12-27")
				.address("Test Address 1")
				.onlineType("OFFLINE")
				.hashtags(List.of("test", "event"))
				.remainDays("D-5")
				.build(),
			EventListResponseDTO.builder()
				.id(2L)
				.title("Test Event 2")
				.bannerImageUrl("http://example.com/banner2.jpg")
				.hostChannelName("Test Channel 2")
				.startDate("2024-12-28")
				.address("Test Address 2")
				.onlineType("ONLINE")
				.hashtags(List.of("bookmark", "test"))
				.remainDays("D-3")
				.build()
		);

		given(bookmarkService.getUserBookmarks(any(Long.class))).willReturn(bookmarkList);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/events/{eventId}/bookmark", eventId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].title").value("Test Event 1"))
			.andExpect(jsonPath("$.result[0].hostChannelName").value("Test Channel 1"))
			.andExpect(jsonPath("$.result[1].id").value(2L))
			.andExpect(jsonPath("$.result[1].title").value("Test Event 2"))
			.andExpect(jsonPath("$.result[1].hostChannelName").value("Test Channel 2"))
			.andDo(print());

		verify(bookmarkService).getUserBookmarks(any(Long.class));
	}

	@Test
	@DisplayName("북마크 삭제")
	void deleteBookmark() throws Exception {
		// GIVEN
		Long eventId = 1L;
		Long bookmarkId = 1L;

		willDoNothing().given(bookmarkService).deleteBookmark(bookmarkId);

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/events/{eventId}/bookmark/{bookmarkId}", eventId, bookmarkId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("북마크 삭제 성공"))
			.andDo(print());

		verify(bookmarkService).deleteBookmark(bookmarkId);
	}
} 