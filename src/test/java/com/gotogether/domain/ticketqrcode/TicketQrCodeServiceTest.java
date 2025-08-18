package com.gotogether.domain.ticketqrcode;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

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
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCodeStatus;
import com.gotogether.domain.ticketqrcode.repository.TicketQrCodeRepository;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeServiceImpl;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class TicketQrCodeServiceTest {

	@InjectMocks
	private TicketQrCodeServiceImpl ticketQrCodeService;

	@Mock
	private TicketQrCodeRepository ticketQrCodeRepository;

	@Mock
	private EventFacade eventFacade;

	@Mock
	private MetricService metricService;

	private Order order;
	private TicketQrCode ticketQrCode;
	private User user;
	private Event event;
	private Ticket ticket;
	private HostChannel hostChannel;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(ticketQrCodeService, "qrSecretKey", "test-secret-key");

		user = User.builder()
			.name("Test User")
			.email("test@example.com")
			.provider("google")
			.providerId("123")
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();
		ReflectionTestUtils.setField(hostChannel, "id", 1L);

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

		order = Order.builder()
			.user(user)
			.ticket(ticket)
			.status(OrderStatus.COMPLETED)
			.build();
		ReflectionTestUtils.setField(order, "id", 1L);

		ticketQrCode = TicketQrCode.builder()
			.order(order)
			.status(TicketQrCodeStatus.AVAILABLE)
			.qrCodeImageUrl("http://example.com/qr.png")
			.build();
		ReflectionTestUtils.setField(ticketQrCode, "id", 1L);

		order.updateTicketQrCode(ticketQrCode);
	}

	@Test
	@DisplayName("QR 코드 생성")
	void createQrCode() {
		// GIVEN
		when(ticketQrCodeRepository.save(any(TicketQrCode.class))).thenReturn(ticketQrCode);

		// WHEN
		TicketQrCode result = ticketQrCodeService.createQrCode(order);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(TicketQrCodeStatus.AVAILABLE);
	}

	@Test
	@DisplayName("QR 코드 삭제")
	void deleteQrCode() {
		// GIVEN
		Long orderId = 1L;

		// WHEN
		ticketQrCodeService.deleteQrCode(orderId);

		// THEN
		verify(ticketQrCodeRepository).deleteByOrderId(orderId);
	}

	@Test
	@DisplayName("유효한 서명으로 QR 코드 검증")
	void validateSignedQrCode() {
		// GIVEN
		Long orderId = 1L;
		String orderData = "orderId-" + orderId;
		String signature = ReflectionTestUtils.invokeMethod(ticketQrCodeService, "hmacSHA256", orderData,
			"test-secret-key");

		when(eventFacade.getOrderById(orderId)).thenReturn(order);

		// WHEN & THEN
		assertThatNoException().isThrownBy(() ->
			ticketQrCodeService.validateSignedQrCode(orderId, signature)
		);
	}
} 