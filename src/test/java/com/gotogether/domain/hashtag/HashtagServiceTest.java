package com.gotogether.domain.hashtag;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.eventhashtag.repository.EventHashtagRepository;
import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.dto.response.HashtagListResponseDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.repository.HashtagRepository;
import com.gotogether.domain.hashtag.service.HashtagServiceImpl;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

	@Mock
	private HashtagRepository hashtagRepository;

	@Mock
	private EventHashtagRepository eventHashtagRepository;

	@InjectMocks
	private HashtagServiceImpl hashtagService;

	private Hashtag hashtag;
	private Event event;
	private HashtagRequestDTO requestDTO;

	@BeforeEach
	void setUp() {
		hashtag = Hashtag.builder()
			.name("test")
			.build();

		event = Event.builder()
			.title("Test Ticket")
			.description("This is a test event.")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(7))
			.bannerImageUrl("https://example.com/banner.jpg")
			.address("Test Location")
			.locationLat(100.0)
			.locationLng(200.0)
			.onlineType(OnlineType.ONLINE)
			.category(Category.DEVELOPMENT_STUDY)
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.hostChannel(null)
			.build();

		requestDTO = HashtagRequestDTO.builder()
			.hashtagName("test")
			.build();
	}

	@Test
	@DisplayName("해시태그 생성")
	void createHashtag() {
		// GIVEN
		when(hashtagRepository.findByName(any())).thenReturn(Optional.empty());
		when(hashtagRepository.save(any())).thenReturn(hashtag);

		// WHEN
		Hashtag result = hashtagService.createHashtag(requestDTO);

		// THEN
		assertThat(result.getName()).isEqualTo("test");
		verify(hashtagRepository).save(any());
	}

	@Test
	@DisplayName("해시태그 목록 조회")
	void getHashtags() {
		// GIVEN
		Pageable pageable = PageRequest.of(0, 10);
		List<Hashtag> hashtags = Arrays.asList(hashtag);
		Page<Hashtag> hashtagPage = new PageImpl<>(hashtags, pageable, hashtags.size());

		when(hashtagRepository.findAll(pageable)).thenReturn(hashtagPage);

		// WHEN
		Page<HashtagListResponseDTO> result = hashtagService.getHashtags(pageable);

		// THEN
		assertThat(result.getContent().get(0).getHashtagName()).isEqualTo("test");
	}

	@Test
	@DisplayName("해시태그 수정")
	void updateHashtag() {
		// GIVEN
		when(hashtagRepository.findById(any())).thenReturn(Optional.of(hashtag));
		when(hashtagRepository.save(any())).thenReturn(hashtag);

		// WHEN
		Hashtag result = hashtagService.updateHashtag(1L, requestDTO);

		// THEN
		assertThat(result.getName()).isEqualTo("test");
		verify(hashtagRepository).save(any());
	}

	@Test
	@DisplayName("해시태그 삭제")
	void deleteHashtag() {
		// GIVEN
		when(hashtagRepository.findById(any())).thenReturn(Optional.of(hashtag));
		doNothing().when(hashtagRepository).delete(any());

		// WHEN
		hashtagService.deleteHashtag(1L);

		// THEN
		verify(hashtagRepository).delete(any());
	}

	@Test
	@DisplayName("이벤트에 해시태그 생성")
	void createHashtags() {
		// GIVEN
		List<String> hashtags = Arrays.asList("test1", "test2");

		when(hashtagRepository.findByName(any())).thenReturn(Optional.empty());
		when(hashtagRepository.save(any())).thenReturn(hashtag);
		when(eventHashtagRepository.saveAll(any())).thenReturn(List.of());

		// WHEN
		hashtagService.createHashtags(event, hashtags);

		// THEN
		verify(eventHashtagRepository).saveAll(any());
	}

	@Test
	@DisplayName("이벤트의 해시태그 삭제")
	void deleteHashtagsByEvent() {
		// GIVEN
		List<Hashtag> hashtags = Arrays.asList(hashtag);

		when(eventHashtagRepository.findHashtagsByEvent(any())).thenReturn(hashtags);
		when(eventHashtagRepository.countByHashtag(any())).thenReturn(0);

		// WHEN
		hashtagService.deleteHashtagsByEvent(event);

		// THEN
		verify(eventHashtagRepository).deleteByEvent(any());
		verify(hashtagRepository).delete(any());
	}
} 