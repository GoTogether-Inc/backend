package com.gotogether.domain.bookmark;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.repository.BookmarkRepository;
import com.gotogether.domain.bookmark.service.BookmarkServiceImpl;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

	@InjectMocks
	private BookmarkServiceImpl bookmarkService;

	@Mock
	private BookmarkRepository bookmarkRepository;

	@Mock
	private EventFacade eventFacade;

	@Mock
	private MetricService metricService;

	private User user;
	private Event event;
	private HostChannel hostChannel;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.name("Test User")
			.email("test@example.com")
			.provider("google")
			.providerId("123")
			.build();

		hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();

		event = Event.builder()
			.title("Test Event")
			.description("Test Description")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.bannerImageUrl("http://example.com/banner.jpg")
			.address("Test Address")
			.locationLat(37.5665)
			.locationLng(126.9780)
			.onlineType(OnlineType.OFFLINE)
			.category(Category.DEVELOPMENT_STUDY)
			.organizerEmail("organizer@example.com")
			.organizerPhoneNumber("010-9876-5432")
			.hostChannel(hostChannel)
			.build();

	}

	@Test
	@DisplayName("북마크 생성")
	void createBookmark() {
		// GIVEN
		Long eventId = 1L;
		Long userId = 1L;

		Bookmark savedBookmark = Bookmark.builder()
			.user(user)
			.event(event)
			.build();

		when(eventFacade.getEventById(eventId)).thenReturn(event);
		when(eventFacade.getUserById(userId)).thenReturn(user);
		when(bookmarkRepository.existsByEventAndUser(event, user)).thenReturn(false);
		when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(savedBookmark);

		// WHEN
		Bookmark result = bookmarkService.createBookmark(eventId, userId);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getUser()).isEqualTo(user);
		assertThat(result.getEvent()).isEqualTo(event);

		verify(eventFacade).getEventById(eventId);
		verify(eventFacade).getUserById(userId);
		verify(bookmarkRepository).existsByEventAndUser(event, user);
		verify(bookmarkRepository).save(any(Bookmark.class));
		verify(metricService).recordBookmarkCreation(eventId);
	}

	@Test
	@DisplayName("사용자 북마크 목록 조회")
	void getUserBookmarks() {
		// GIVEN
		Long userId = 1L;

		Bookmark bookmark1 = Bookmark.builder()
			.user(user)
			.event(event)
			.build();

		Event event2 = Event.builder()
			.title("Test Event 2")
			.description("Test Description 2")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(2))
			.bannerImageUrl("http://example.com/banner2.jpg")
			.address("Test Address 2")
			.locationLat(37.5665)
			.locationLng(126.9780)
			.onlineType(OnlineType.ONLINE)
			.category(Category.CONFERENCE)
			.organizerEmail("organizer2@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.hostChannel(hostChannel)
			.build();

		Bookmark bookmark2 = Bookmark.builder()
			.user(user)
			.event(event2)
			.build();

		List<Bookmark> bookmarks = Arrays.asList(bookmark1, bookmark2);
		when(bookmarkRepository.findByUserId(userId)).thenReturn(bookmarks);

		// WHEN
		List<EventListResponseDTO> result = bookmarkService.getUserBookmarks(userId);

		// THEN
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getTitle()).isEqualTo("Test Event");
		assertThat(result.get(1).getTitle()).isEqualTo("Test Event 2");

		verify(bookmarkRepository).findByUserId(userId);
	}

	@Test
	@DisplayName("북마크 삭제")
	void deleteBookmark() {
		// GIVEN
		Long bookmarkId = 1L;

		Bookmark existingBookmark = Bookmark.builder()
			.user(user)
			.event(event)
			.build();

		when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(existingBookmark));

		// WHEN
		bookmarkService.deleteBookmark(bookmarkId);

		// THEN
		verify(bookmarkRepository).findById(bookmarkId);
		verify(bookmarkRepository).delete(existingBookmark);
	}
} 