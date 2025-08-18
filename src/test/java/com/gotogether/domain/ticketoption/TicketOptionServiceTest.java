package com.gotogether.domain.ticketoption;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketoption.dto.request.TicketOptionRequestDTO;
import com.gotogether.domain.ticketoption.dto.response.TicketOptionDetailResponseDTO;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionStatus;
import com.gotogether.domain.ticketoption.entity.TicketOptionType;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoption.service.TicketOptionServiceImpl;
import com.gotogether.domain.ticketoptionanswer.repository.TicketOptionAnswerRepository;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class TicketOptionServiceTest {

	@InjectMocks
	private TicketOptionServiceImpl ticketOptionService;

	@Mock
	private TicketOptionRepository ticketOptionRepository;

	@Mock
	private TicketOptionChoiceRepository ticketOptionChoiceRepository;

	@Mock
	private TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private TicketOptionAnswerRepository ticketOptionAnswerRepository;

	@Mock
	private MetricService metricService;

	private Ticket ticket;
	private TicketOption ticketOption;
	private TicketOptionRequestDTO singleTypeRequest;
	private TicketOptionRequestDTO textTypeRequest;

	@BeforeEach
	void setUp() {
		HostChannel hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();
		ReflectionTestUtils.setField(hostChannel, "id", 1L);

		Event event = Event.builder()
			.title("Test Event")
			.description("Test Description")
			.startDate(LocalDateTime.now().plusDays(1))
			.endDate(LocalDateTime.now().plusDays(2))
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
		ReflectionTestUtils.setField(event, "id", 1L);

		ticket = Ticket.builder()
			.event(event)
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.name("Test Ticket")
			.price(10000)
			.availableQuantity(100)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.build();
		ReflectionTestUtils.setField(ticket, "id", 1L);

		List<String> choices = Arrays.asList("한식", "중식", "양식");

		singleTypeRequest = TicketOptionRequestDTO.builder()
			.eventId(1L)
			.name("점심 메뉴 선택")
			.description("컨퍼런스 점심 메뉴를 선택해주세요")
			.type(TicketOptionType.SINGLE)
			.isMandatory(true)
			.choices(choices)
			.build();

		textTypeRequest = TicketOptionRequestDTO.builder()
			.eventId(1L)
			.name("추가 요청사항")
			.description("알레르기나 기타 요청사항이 있으시면 작성해주세요")
			.type(TicketOptionType.TEXT)
			.isMandatory(false)
			.build();

		ticketOption = TicketOption.builder()
			.eventId(1L)
			.name("점심 메뉴 선택")
			.description("컨퍼런스 점심 메뉴를 선택해주세요")
			.type(TicketOptionType.SINGLE)
			.isMandatory(true)
			.build();
		ReflectionTestUtils.setField(ticketOption, "id", 1L);
	}

	@Test
	@DisplayName("티켓 옵션 생성 - SINGLE 타입")
	void createTicketOption_SingleType() {
		// GIVEN
		given(ticketOptionRepository.save(any(TicketOption.class))).willAnswer(invocation -> {
			TicketOption saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// WHEN
		TicketOption result = ticketOptionService.createTicketOption(singleTypeRequest);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("점심 메뉴 선택");
		assertThat(result.getType()).isEqualTo(TicketOptionType.SINGLE);
		assertThat(result.isMandatory()).isTrue();

		verify(ticketOptionRepository).save(any(TicketOption.class));
		verify(ticketOptionChoiceRepository).saveAll(anyList());
		verify(metricService).recordTicketOptionCreation(eq(1L));
	}

	@Test
	@DisplayName("티켓 옵션 생성 - TEXT 타입")
	void createTicketOption_TextType() {
		// GIVEN
		given(ticketOptionRepository.save(any(TicketOption.class))).willAnswer(invocation -> {
			TicketOption saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// WHEN
		TicketOption result = ticketOptionService.createTicketOption(textTypeRequest);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("추가 요청사항");
		assertThat(result.getType()).isEqualTo(TicketOptionType.TEXT);
		assertThat(result.isMandatory()).isFalse();

		verify(ticketOptionRepository).save(any(TicketOption.class));
		verify(ticketOptionChoiceRepository, never()).saveAll(anyList());
		verify(metricService).recordTicketOptionCreation(eq(1L));
	}

	@Test
	@DisplayName("이벤트별 티켓 옵션 목록 조회")
	void getTicketOptionsByEventId() {
		// GIVEN
		Long eventId = 1L;
		List<TicketOptionStatus> visibleStatuses = List.of(
			TicketOptionStatus.CREATED,
			TicketOptionStatus.ASSIGNED
		);
		List<TicketOption> ticketOptions = Collections.singletonList(ticketOption);

		given(ticketOptionRepository.findAllByEventIdAndStatusIn(eventId, visibleStatuses))
			.willReturn(ticketOptions);

		// WHEN
		List<TicketOptionDetailResponseDTO> result = ticketOptionService.getTicketOptionsByEventId(eventId);

		// THEN
		assertThat(result).hasSize(1);
		verify(ticketOptionRepository).findAllByEventIdAndStatusIn(eventId, visibleStatuses);
	}

	@Test
	@DisplayName("티켓별 티켓 옵션 목록 조회")
	void getTicketOptionsByTicketId() {
		// GIVEN
		Long ticketId = 1L;
		TicketOptionAssignment assignment = TicketOptionAssignment.builder()
			.ticket(ticket)
			.ticketOption(ticketOption)
			.build();
		List<TicketOptionAssignment> assignments = Collections.singletonList(assignment);

		given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
		given(ticketOptionAssignmentRepository.findAllByTicket(ticket)).willReturn(assignments);

		// WHEN
		List<TicketOptionDetailResponseDTO> result = ticketOptionService.getTicketOptionsByTicketId(ticketId);

		// THEN
		assertThat(result).hasSize(1);
		verify(ticketRepository).findById(ticketId);
		verify(ticketOptionAssignmentRepository).findAllByTicket(ticket);
	}

	@Test
	@DisplayName("티켓 옵션 상세 조회")
	void getTicketOption() {
		// GIVEN
		Long ticketOptionId = 1L;

		given(ticketOptionRepository.findById(ticketOptionId)).willReturn(Optional.of(ticketOption));

		// WHEN
		TicketOptionDetailResponseDTO result = ticketOptionService.getTicketOption(ticketOptionId);

		// THEN
		assertThat(result).isNotNull();
		verify(ticketOptionRepository).findById(ticketOptionId);
	}

	@Test
	@DisplayName("티켓 옵션 수정")
	void updateTicketOption() {
		// GIVEN
		Long ticketOptionId = 1L;
		TicketOptionRequestDTO updateRequest = TicketOptionRequestDTO.builder()
			.eventId(1L)
			.name("교통편 옵션")
			.description("컨퍼런스 참석 시 이용할 교통편을 선택해주세요 (복수 선택 가능)")
			.type(TicketOptionType.MULTIPLE)
			.isMandatory(false)
			.choices(Arrays.asList("지하철", "버스"))
			.build();

		given(ticketOptionRepository.findById(ticketOptionId)).willReturn(Optional.of(ticketOption));

		// WHEN
		TicketOption result = ticketOptionService.updateTicketOption(ticketOptionId, updateRequest);

		// THEN
		assertThat(result).isNotNull();
		verify(ticketOptionRepository).findById(ticketOptionId);
	}

	@Test
	@DisplayName("티켓 옵션 삭제")
	void deleteTicketOption() {
		// GIVEN
		Long ticketOptionId = 1L;
		List<TicketOptionAssignment> assignments = Collections.emptyList();

		given(ticketOptionRepository.findById(ticketOptionId)).willReturn(Optional.of(ticketOption));
		given(ticketOptionAnswerRepository.existsByTicketOption(ticketOption)).willReturn(false);
		given(ticketOptionAssignmentRepository.findAllByTicketOption(ticketOption)).willReturn(assignments);

		// WHEN
		ticketOptionService.deleteTicketOption(ticketOptionId);

		// THEN
		verify(ticketOptionRepository).findById(ticketOptionId);
		verify(ticketOptionAnswerRepository).existsByTicketOption(ticketOption);
		verify(ticketOptionAssignmentRepository).findAllByTicketOption(ticketOption);
		verify(ticketOptionRepository).delete(ticketOption);
	}
} 