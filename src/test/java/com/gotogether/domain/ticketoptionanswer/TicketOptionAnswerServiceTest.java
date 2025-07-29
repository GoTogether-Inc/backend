package com.gotogether.domain.ticketoptionanswer;

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
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionChoice;
import com.gotogether.domain.ticketoption.entity.TicketOptionType;
import com.gotogether.domain.ticketoption.repository.TicketOptionChoiceRepository;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionanswer.dto.request.TicketOptionAnswerRequestDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerDetailResponseDTO;
import com.gotogether.domain.ticketoptionanswer.dto.response.PurchaserAnswerListResponseDTO;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;
import com.gotogether.domain.ticketoptionanswer.repository.TicketOptionAnswerRepository;
import com.gotogether.domain.ticketoptionanswer.service.TicketOptionAnswerServiceImpl;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class TicketOptionAnswerServiceTest {

	@InjectMocks
	private TicketOptionAnswerServiceImpl ticketOptionAnswerService;

	@Mock
	private TicketOptionRepository ticketOptionRepository;

	@Mock
	private TicketOptionChoiceRepository ticketOptionChoiceRepository;

	@Mock
	private TicketOptionAnswerRepository ticketOptionAnswerRepository;

	@Mock
	private TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MetricService metricService;

	private User user;
	private Ticket ticket;
	private TicketOption singleTicketOption;
	private TicketOption textTicketOption;
	private TicketOptionChoice choice1;
	private TicketOptionAnswer answer;

	@BeforeEach
	void setUp() {
		HostChannel hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();
		ReflectionTestUtils.setField(hostChannel, "id", 1L);

		user = User.builder()
			.name("Test User")
			.email("test@example.com")
			.provider("google")
			.providerId("123")
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

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

		Order order = Order.builder()
			.user(user)
			.ticket(ticket)
			.status(OrderStatus.COMPLETED)
			.build();
		ReflectionTestUtils.setField(order, "id", 1L);

		singleTicketOption = TicketOption.builder()
			.eventId(1L)
			.name("굿즈 티셔츠 사이즈")
			.description("컨퍼런스 굿즈로 제공되는 티셔츠 사이즈를 선택해주세요")
			.type(TicketOptionType.SINGLE)
			.isMandatory(true)
			.build();
		ReflectionTestUtils.setField(singleTicketOption, "id", 1L);

		TicketOption multipleTicketOption = TicketOption.builder()
			.eventId(1L)
			.name("관심 기술 스택")
			.description("현재 관심있는 기술 스택을 모두 선택해주세요 (복수 선택 가능)")
			.type(TicketOptionType.MULTIPLE)
			.isMandatory(false)
			.build();
		ReflectionTestUtils.setField(multipleTicketOption, "id", 2L);

		textTicketOption = TicketOption.builder()
			.eventId(1L)
			.name("특별 요청사항")
			.description("컨퍼런스 참석과 관련하여 특별한 요청사항이 있으시면 작성해주세요")
			.type(TicketOptionType.TEXT)
			.isMandatory(false)
			.build();
		ReflectionTestUtils.setField(textTicketOption, "id", 3L);

		choice1 = TicketOptionChoice.builder()
			.ticketOption(singleTicketOption)
			.name("M")
			.build();
		ReflectionTestUtils.setField(choice1, "id", 1L);

		TicketOptionChoice choice2 = TicketOptionChoice.builder()
			.ticketOption(singleTicketOption)
			.name("L")
			.build();
		ReflectionTestUtils.setField(choice2, "id", 2L);

		answer = TicketOptionAnswer.builder()
			.user(user)
			.order(order)
			.ticketOption(singleTicketOption)
			.ticketOptionChoice(choice1)
			.answerText(null)
			.build();
		ReflectionTestUtils.setField(answer, "id", 1L);
	}

	@Test
	@DisplayName("티켓 옵션 답변 생성 - 단일 선택")
	void createTicketOptionAnswer_SingleChoice() {
		// GIVEN
		Long userId = 1L;
		TicketOptionAnswerRequestDTO request = TicketOptionAnswerRequestDTO.builder()
			.ticketOptionId(1L)
			.ticketOptionChoiceId(1L)
			.build();

		given(ticketOptionRepository.findById(1L)).willReturn(Optional.of(singleTicketOption));
		given(ticketOptionAnswerRepository.existsByUserIdAndTicketOptionId(userId, 1L)).willReturn(false);
		given(ticketOptionChoiceRepository.findById(1L)).willReturn(Optional.of(choice1));
		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// WHEN
		ticketOptionAnswerService.createTicketOptionAnswer(userId, request);

		// THEN
		verify(ticketOptionRepository).findById(1L);
		verify(ticketOptionAnswerRepository).existsByUserIdAndTicketOptionId(userId, 1L);
		verify(ticketOptionChoiceRepository).findById(1L);
		verify(userRepository).findById(userId);
		verify(ticketOptionAnswerRepository).save(any(TicketOptionAnswer.class));
		verify(metricService).recordTicketOptionAnswerCreation(any());
	}

	@Test
	@DisplayName("티켓 옵션 답변 생성 - 텍스트 입력")
	void createTicketOptionAnswer_Text() {
		// GIVEN
		Long userId = 1L;
		TicketOptionAnswerRequestDTO request = TicketOptionAnswerRequestDTO.builder()
			.ticketOptionId(3L)
			.answerText("Test Answer")
			.build();

		given(ticketOptionRepository.findById(3L)).willReturn(Optional.of(textTicketOption));
		given(ticketOptionAnswerRepository.existsByUserIdAndTicketOptionId(userId, 3L)).willReturn(false);
		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// WHEN
		ticketOptionAnswerService.createTicketOptionAnswer(userId, request);

		// THEN
		verify(ticketOptionRepository).findById(3L);
		verify(ticketOptionAnswerRepository).existsByUserIdAndTicketOptionId(userId, 3L);
		verify(ticketOptionChoiceRepository, never()).findById(any());
		verify(userRepository).findById(userId);
		verify(ticketOptionAnswerRepository).save(any(TicketOptionAnswer.class));
	}

	@Test
	@DisplayName("티켓별 답변 조회")
	void getAnswersByTicket() {
		// GIVEN
		Long ticketId = 1L;
		List<TicketOptionAnswer> answers = Collections.singletonList(answer);

		given(ticketOptionAnswerRepository.findByTicketId(ticketId)).willReturn(answers);

		// WHEN
		List<PurchaserAnswerDetailResponseDTO> result = ticketOptionAnswerService.getAnswersByTicket(ticketId);

		// THEN
		assertThat(result).hasSize(1);
		verify(ticketOptionAnswerRepository).findByTicketId(ticketId);
	}

	@Test
	@DisplayName("구매자 답변 목록 조회")
	void getPurchaserAnswers() {
		// GIVEN
		Long ticketId = 1L;
		TicketOptionAssignment assignment = TicketOptionAssignment.builder()
			.ticket(ticket)
			.ticketOption(singleTicketOption)
			.build();
		List<TicketOptionAssignment> assignments = Collections.singletonList(assignment);
		List<TicketOptionAnswer> answers = Collections.singletonList(answer);

		given(ticketOptionAssignmentRepository.findAllByTicketId(ticketId)).willReturn(assignments);
		given(ticketOptionAssignmentRepository.findAllByTicket(ticket)).willReturn(assignments);
		given(ticketOptionAnswerRepository.findByTicketOptionIdIn(Arrays.asList(1L))).willReturn(answers);

		// WHEN
		PurchaserAnswerListResponseDTO result = ticketOptionAnswerService.getPurchaserAnswers(ticketId);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getOrderCount()).isEqualTo(1);
		assertThat(result.getTicketOptions()).hasSize(1);
		verify(ticketOptionAssignmentRepository).findAllByTicketId(ticketId);
		verify(ticketOptionAssignmentRepository).findAllByTicket(ticket);
		verify(ticketOptionAnswerRepository).findByTicketOptionIdIn(anyList());
	}
} 