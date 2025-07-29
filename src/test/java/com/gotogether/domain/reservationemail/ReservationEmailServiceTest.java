package com.gotogether.domain.reservationemail;

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
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.reservationemail.dto.request.ReservationEmailRequestDTO;
import com.gotogether.domain.reservationemail.dto.response.ReservationEmailDetailResponseDTO;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.entity.ReservationEmailStatus;
import com.gotogether.domain.reservationemail.entity.ReservationEmailTargetType;
import com.gotogether.domain.reservationemail.facade.ReservationEmailFacade;
import com.gotogether.domain.reservationemail.repository.ReservationEmailRepository;
import com.gotogether.domain.reservationemail.service.EmailService;
import com.gotogether.domain.reservationemail.service.ReservationEmailServiceImpl;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.global.scheduler.EventScheduler;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class ReservationEmailServiceTest {

	@InjectMocks
	private ReservationEmailServiceImpl reservationEmailService;

	@Mock
	private ReservationEmailRepository reservationEmailRepository;

	@Mock
	private EmailService emailService;

	@Mock
	private MetricService metricService;

	@Mock
	private EventFacade eventFacade;

	@Mock
	private ReservationEmailFacade reservationEmailFacade;

	@Mock
	private EventScheduler eventScheduler;

	private Event event;
	private Ticket ticket;
	private ReservationEmailRequestDTO request;
	private ReservationEmail reservationEmail;
	private List<String> recipients;

	@BeforeEach
	void setUp() {
		HostChannel hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();
		ReflectionTestUtils.setField(hostChannel, "id", 1L);

		event = Event.builder()
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

		ReflectionTestUtils.setField(event, "tickets", List.of(ticket));

		recipients = Arrays.asList("user1@example.com", "user2@example.com");

		request = ReservationEmailRequestDTO.builder()
			.eventId(1L)
			.targetType(ReservationEmailTargetType.ALL)
			.recipients(recipients)
			.title("Test Email")
			.content("Test Content")
			.reservationDate(LocalDateTime.now().plusHours(1))
			.build();

		reservationEmail = ReservationEmail.builder()
			.event(event)
			.targetType(ReservationEmailTargetType.ALL)
			.recipients(recipients)
			.title("Test Email")
			.content("Test Content")
			.reservationDate(LocalDateTime.now().plusHours(1))
			.build();
		ReflectionTestUtils.setField(reservationEmail, "id", 1L);
	}

	@Test
	@DisplayName("예약 이메일 생성")
	void createReservationEmail_AllType() {
		// GIVEN
		given(eventFacade.getEventById(1L)).willReturn(event);
		given(reservationEmailRepository.save(any(ReservationEmail.class))).willAnswer(invocation -> {
			ReservationEmail saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// WHEN
		ReservationEmail result = reservationEmailService.createReservationEmail(request);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getEvent()).isEqualTo(event);
		assertThat(result.getTargetType()).isEqualTo(ReservationEmailTargetType.ALL);
		assertThat(result.getRecipients()).isEqualTo(recipients);
		assertThat(result.getTitle()).isEqualTo("Test Email");

		verify(eventFacade).getEventById(1L);
		verify(reservationEmailRepository).save(any(ReservationEmail.class));
		verify(eventScheduler).scheduleEmail(eq(1L), any(LocalDateTime.class));
		verify(metricService).recordReservationEmailCreation(eq(1L));
	}

	@Test
	@DisplayName("예약 이메일 목록 조회")
	void getReservationEmails_WithStatusFilter() {
		// GIVEN
		Long eventId = 1L;
		String status = "PENDING";
		List<ReservationEmail> reservationEmails = Collections.singletonList(reservationEmail);

		given(reservationEmailRepository.findByEventIdAndStatus(eventId, ReservationEmailStatus.PENDING))
			.willReturn(reservationEmails);

		// WHEN
		List<ReservationEmailDetailResponseDTO> result = reservationEmailService.getReservationEmails(eventId, status);

		// THEN
		assertThat(result).hasSize(1);
		verify(reservationEmailRepository).findByEventIdAndStatus(eventId, ReservationEmailStatus.PENDING);
		verify(reservationEmailRepository, never()).findByEventId(any());
	}

	@Test
	@DisplayName("예약 이메일 수정")
	void updateReservationEmail() {
		// GIVEN
		Long reservationEmailId = 1L;

		given(reservationEmailFacade.getReservationEmailById(reservationEmailId)).willReturn(reservationEmail);
		given(eventFacade.getEventById(1L)).willReturn(event);
		given(reservationEmailRepository.save(reservationEmail)).willReturn(reservationEmail);

		// WHEN
		ReservationEmail result = reservationEmailService.updateReservationEmail(reservationEmailId, request);

		// THEN
		assertThat(result).isNotNull();
		verify(reservationEmailFacade).getReservationEmailById(reservationEmailId);
		verify(eventFacade).getEventById(1L);
		verify(reservationEmailRepository).save(reservationEmail);
		verify(eventScheduler).deleteScheduledEmailJob(any(Long.class));
		verify(eventScheduler).scheduleEmail(any(Long.class), any(LocalDateTime.class));
	}

	@Test
	@DisplayName("예약 이메일 삭제")
	void deleteReservationEmail() {
		// GIVEN
		Long reservationEmailId = 1L;

		given(reservationEmailFacade.getReservationEmailById(reservationEmailId)).willReturn(reservationEmail);

		// WHEN
		reservationEmailService.deleteReservationEmail(reservationEmailId);

		// THEN
		verify(reservationEmailFacade).getReservationEmailById(reservationEmailId);
		verify(eventScheduler).deleteScheduledEmailJob(any(Long.class));
		verify(reservationEmailRepository).delete(reservationEmail);
	}

	@Test
	@DisplayName("예약 이메일 발송")
	void sendReservationEmail() {
		// GIVEN
		Long reservationEmailId = 1L;

		given(reservationEmailRepository.findById(reservationEmailId))
			.willReturn(Optional.of(reservationEmail));
		given(reservationEmailRepository.save(reservationEmail)).willReturn(reservationEmail);

		// WHEN
		reservationEmailService.sendReservationEmail(reservationEmailId);

		// THEN
		verify(reservationEmailRepository).findById(reservationEmailId);
		verify(emailService).sendEmail(
			reservationEmail.getRecipients().toArray(new String[0]),
			reservationEmail.getTitle(),
			reservationEmail.getContent()
		);
		verify(metricService).recordReservationEmailDispatch(any(Long.class));
		verify(reservationEmailRepository).save(reservationEmail);

		assertThat(reservationEmail.getStatus()).isEqualTo(ReservationEmailStatus.SENT);
	}
} 