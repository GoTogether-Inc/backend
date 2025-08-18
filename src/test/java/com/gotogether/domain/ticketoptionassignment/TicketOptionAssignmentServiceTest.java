package com.gotogether.domain.ticketoptionassignment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
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
import com.gotogether.domain.ticketoption.entity.TicketOption;
import com.gotogether.domain.ticketoption.entity.TicketOptionStatus;
import com.gotogether.domain.ticketoption.entity.TicketOptionType;
import com.gotogether.domain.ticketoption.repository.TicketOptionRepository;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.domain.ticketoptionassignment.service.TicketOptionAssignmentServiceImpl;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class TicketOptionAssignmentServiceTest {

	@InjectMocks
	private TicketOptionAssignmentServiceImpl ticketOptionAssignmentService;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private TicketOptionRepository ticketOptionRepository;

	@Mock
	private TicketOptionAssignmentRepository ticketOptionAssignmentRepository;

	@Mock
	private MetricService metricService;

	private Ticket ticket;
	private TicketOption createdTicketOption;
	private TicketOptionAssignment assignment;

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

		createdTicketOption = TicketOption.builder()
			.eventId(1L)
			.name("점심 식사 옵션")
			.description("컨퍼런스 점심 메뉴를 선택해주세요")
			.type(TicketOptionType.SINGLE)
			.isMandatory(true)
			.build();
		ReflectionTestUtils.setField(createdTicketOption, "id", 1L);

		TicketOption assignedTicketOption = TicketOption.builder()
			.eventId(1L)
			.name("네트워킹 세션 참여")
			.description("저녁 네트워킹 세션에 참여하시겠습니까?")
			.type(TicketOptionType.SINGLE)
			.isMandatory(false)
			.build();
		ReflectionTestUtils.setField(assignedTicketOption, "id", 2L);
		assignedTicketOption.markAsAssigned();

		assignment = TicketOptionAssignment.builder()
			.ticket(ticket)
			.ticketOption(createdTicketOption)
			.build();
		ReflectionTestUtils.setField(assignment, "id", 1L);
	}

	@Test
	@DisplayName("티켓 옵션 할당")
	void assignTicketOption_CreatedStatus() {
		// GIVEN
		Long ticketId = 1L;
		Long ticketOptionId = 1L;

		given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
		given(ticketOptionRepository.findById(ticketOptionId)).willReturn(Optional.of(createdTicketOption));
		given(ticketOptionAssignmentRepository.existsByTicketIdAndTicketOptionId(ticketId, ticketOptionId))
			.willReturn(false);

		// WHEN
		ticketOptionAssignmentService.assignTicketOption(ticketId, ticketOptionId);

		// THEN
		verify(ticketRepository).findById(ticketId);
		verify(ticketOptionRepository).findById(ticketOptionId);
		verify(ticketOptionAssignmentRepository).existsByTicketIdAndTicketOptionId(ticketId, ticketOptionId);
		verify(ticketOptionAssignmentRepository).save(any(TicketOptionAssignment.class));
		verify(metricService).recordTicketOptionAssignment(ticketOptionId, ticketId);

		assertThat(createdTicketOption.getStatus()).isEqualTo(TicketOptionStatus.ASSIGNED);
	}

	@Test
	@DisplayName("티켓 옵션 할당 해제")
	void unassignTicketOption() {
		// GIVEN
		Long ticketId = 1L;
		Long ticketOptionId = 1L;

		given(ticketOptionAssignmentRepository.findByTicketIdAndTicketOptionId(ticketId, ticketOptionId))
			.willReturn(Optional.of(assignment));

		// WHEN
		ticketOptionAssignmentService.unassignTicketOption(ticketId, ticketOptionId);

		// THEN
		verify(ticketOptionAssignmentRepository).findByTicketIdAndTicketOptionId(ticketId, ticketOptionId);
		verify(ticketOptionAssignmentRepository).delete(assignment);
	}
} 