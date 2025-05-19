package com.gotogether.domain.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.repository.BookmarkRepository;
import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.event.service.EventServiceImpl;
import com.gotogether.domain.hashtag.service.HashtagService;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
import com.gotogether.domain.referencelink.service.ReferenceLinkService;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.scheduler.EventScheduler;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

	@InjectMocks
	private EventServiceImpl eventService;

	@Mock
	private EventRepository eventRepository;
	@Mock
	private HashtagService hashtagService;
	@Mock
	private ReferenceLinkService referenceLinkService;
	@Mock
	private BookmarkRepository bookmarkRepository;
	@Mock
	private EventFacade eventFacade;
	@Mock
	private EventScheduler eventScheduler;

	private User user;
	private Event event;
	private HostChannel hostChannel;
	private EventRequestDTO eventRequestDTO;

	@BeforeEach
	void setUp() {
		hostChannel = HostChannel.builder()
			.name("Test Host Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();

		user = User.builder()
			.name("Test User")
			.email("user@example.com")
			.phoneNumber("010-1234-5678")
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
	void createEvent() {
		// GIVEN
		when(eventFacade.getHostChannelById(any())).thenReturn(hostChannel);
		when(eventRepository.save(any())).thenReturn(event);

		// WHEN
		eventService.createEvent(eventRequestDTO);

		// THEN
		verify(eventRepository).save(any());
		verify(referenceLinkService).createReferenceLinks(any(), any());
		verify(hashtagService).createHashtags(any(), any());
		verify(eventScheduler).scheduleUpdateEventStatus(any(), any());
	}

	@Test
	@DisplayName("이벤트 상세 조회")
	void getDetailEvent() {
		// GIVEN
		when(eventFacade.getEventById(any())).thenReturn(event);
		when(eventFacade.getHostChannelById(any())).thenReturn(hostChannel);
		when(eventFacade.getUserById(any())).thenReturn(user);
		when(bookmarkRepository.findByEventAndUser(any(), any()))
			.thenReturn(java.util.Optional.of(Bookmark.builder()
				.user(user)
				.event(event)
				.build()));

		// WHEN
		EventDetailResponseDTO response = eventService.getDetailEvent(1L, 1L);

		// THEN
		assertNotNull(response);
		assertEquals(event.getTitle(), response.getTitle());
		assertEquals(hostChannel.getName(), response.getHostChannelName());
		verify(eventFacade).getEventById(any());
		verify(eventFacade).getHostChannelById(any());
		verify(bookmarkRepository).findByEventAndUser(any(), any());
	}

	@Test
	@DisplayName("이벤트 수정")
	void updateEvent() {
		// GIVEN
		when(eventFacade.getEventById(any())).thenReturn(event);
		when(eventRepository.save(any())).thenReturn(event);

		// WHEN
		eventService.updateEvent(1L, eventRequestDTO);

		// THEN
		verify(eventRepository).save(any());
		verify(referenceLinkService).deleteAll(any());
		verify(referenceLinkService).createReferenceLinks(any(), any());
		verify(hashtagService).deleteHashtagsByRequest(any(), any());
		verify(hashtagService).createHashtags(any(), any());
	}

	@Test
	@DisplayName("이벤트 삭제")
	void deleteEvent() {
		// GIVEN
		when(eventFacade.getEventById(any())).thenReturn(event);

		// WHEN
		eventService.deleteEvent(1L);

		// THEN
		verify(hashtagService).deleteHashtagsByEvent(any());
		verify(eventScheduler).deleteScheduledEventJob(any());
		verify(eventRepository).delete(any());
	}

	@Test
	@DisplayName("카테고리별 이벤트 조회")
	void getEventsByCategory() {
		// GIVEN
		Pageable pageable = PageRequest.of(0, 10);
		Page<Event> eventPage = new PageImpl<>(List.of(event));
		when(eventRepository.findByCategory(any(), any())).thenReturn(eventPage);

		// WHEN
		Page<EventListResponseDTO> result = eventService.getEventsByCategory(Category.DEVELOPMENT_STUDY, pageable);

		// THEN
		assertEquals("Test Event", result.getContent().get(0).getTitle());
		verify(eventRepository).findByCategory(any(), any());
	}

	@Test
	@DisplayName("이벤트 상태 업데이트")
	void updateEventStatusToCompleted() {
		// GIVEN
		when(eventFacade.getEventById(any())).thenReturn(event);

		// WHEN
		eventService.updateEventStatusToCompleted(1L);

		// THEN
		verify(eventFacade).getEventById(any());
	}
}