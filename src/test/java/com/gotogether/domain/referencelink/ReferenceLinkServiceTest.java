package com.gotogether.domain.referencelink;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
import com.gotogether.domain.referencelink.entity.ReferenceLink;
import com.gotogether.domain.referencelink.repository.ReferenceLinkRepository;
import com.gotogether.domain.referencelink.service.ReferenceLinkServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReferenceLinkServiceTest {

	@InjectMocks
	private ReferenceLinkServiceImpl referenceLinkService;

	@Mock
	private ReferenceLinkRepository referenceLinkRepository;

	private Event event;
	private HostChannel hostChannel;
	private List<ReferenceLinkDTO> referenceLinkDTOList;
	private List<ReferenceLink> referenceLinkList;

	@BeforeEach
	void setUp() {
		hostChannel = HostChannel.builder()
			.name("Test Host Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();

		event = Event.builder()
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
			.hostChannel(hostChannel)
			.build();

		referenceLinkDTOList = List.of(
			ReferenceLinkDTO.builder()
				.title("Test Link")
				.url("https://test.com")
				.build(),
			ReferenceLinkDTO.builder()
				.title("Test Link2")
				.url("https://test2.com")
				.build()
		);

		referenceLinkList = List.of(
			ReferenceLink.builder()
				.event(event)
				.name("Test Link")
				.toGoUrl("https://test.com")
				.build(),
			ReferenceLink.builder()
				.event(event)
				.name("Test Link2")
				.toGoUrl("https://test2.com")
				.build()
		);
	}

	@Test
	@DisplayName("참고 링크 생성")
	void createReferenceLinks() {
		// WHEN
		referenceLinkService.createReferenceLinks(event, referenceLinkDTOList);

		// THEN
		verify(referenceLinkRepository, times(1)).saveAll(any());
	}

	@Test
	@DisplayName("참고 링크 삭제")
	void deleteAllReferenceLinks() {
		// WHEN
		referenceLinkService.deleteAll(referenceLinkList);

		// THEN
		verify(referenceLinkRepository, times(1)).deleteAll(referenceLinkList);
	}
}