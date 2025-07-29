package com.gotogether.domain.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.dto.request.OrderCancelRequestDTO;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderCustomRepository;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.order.service.OrderServiceImpl;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketoptionanswer.service.TicketOptionAnswerService;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCodeStatus;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeService;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@InjectMocks
	private OrderServiceImpl orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private EventFacade eventFacade;

	@Mock
	private TicketQrCodeService ticketQrCodeService;

	@Mock
	private OrderCustomRepository orderCustomRepository;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private TicketOptionAnswerService ticketOptionAnswerService;

	@Mock
	private MetricService metricService;

	private User user;
	private Event event;
	private Ticket ticket;
	private Order order;
	private TicketQrCode ticketQrCode;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.email("test@example.com")
			.build();

		HostChannel hostChannel = HostChannel.builder()
			.name("Test Channel")
			.description("Test Description")
			.build();

		event = Event.builder()
			.title("Updated Event")
			.description("This is a test event")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.bannerImageUrl("https://example.com/updated-banner.jpg")
			.address("Updated Location")
			.locationLat(100.0)
			.locationLng(200.0)
			.onlineType(OnlineType.OFFLINE)
			.category(Category.CONFERENCE)
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.hostChannel(hostChannel)
			.build();

		ticket = Ticket.builder()
			.event(event)
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.availableQuantity(10)
			.name("Test Ticket")
			.price(10000)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.build();

		order = Order.builder()
			.user(user)
			.ticket(ticket)
			.status(OrderStatus.COMPLETED)
			.build();

		ticketQrCode = TicketQrCode.builder()
			.order(order)
			.qrCodeImageUrl("https://example.com/qr-code.png")
			.status(TicketQrCodeStatus.AVAILABLE)
			.build();

		order.updateTicketQrCode(ticketQrCode);
	}

	@Test
	@DisplayName("티켓 주문 생성")
	void createOrder() {
		// GIVEN
		OrderRequestDTO request = OrderRequestDTO.builder()
			.ticketId(1L)
			.eventId(1L)
			.ticketCnt(2)
			.build();

		when(eventFacade.getUserById(any())).thenReturn(user);
		when(ticketRepository.findByIdWithPessimisticLock(any())).thenReturn(Optional.of(ticket));
		when(orderRepository.save(any())).thenReturn(order);
		when(orderRepository.existsByOrderCode(anyString())).thenReturn(false);
		when(ticketQrCodeService.createQrCode(any())).thenReturn(ticketQrCode);

		// WHEN
		List<Order> orders = orderService.createOrder(request, 1L);

		// THEN
		assertThat(orders).hasSize(2);
		verify(ticketQrCodeService, times(2)).createQrCode(any());
	}

	@Test
	@DisplayName("구매한 티켓 목록 조회")
	void getPurchasedTickets() {
		// GIVEN
		when(eventFacade.getUserById(any())).thenReturn(user);
		when(orderCustomRepository.findByUser(any())).thenReturn(List.of(order));

		// WHEN
		List<OrderedTicketResponseDTO> result = orderService.getPurchasedTickets(1L);

		// THEN
		assertThat(result).hasSize(1);
		verify(orderCustomRepository).findByUser(user);
	}

	@Test
	@DisplayName("티켓 구매 확인 정보 조회")
	void getPurchaseConfirmation() {
		// GIVEN
		Long orderId = 1L;

		when(orderRepository.findOrderWithTicketAndEventAndHostById(orderId)).thenReturn(Optional.of(order));

		// WHEN
		OrderInfoResponseDTO result = orderService.getPurchaseConfirmation(orderId);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo(event.getTitle());
		assertThat(result.getTicketName()).isEqualTo(ticket.getName());
		verify(orderRepository).findOrderWithTicketAndEventAndHostById(orderId);
	}

	@Test
	@DisplayName("주문 취소")
	void cancelOrder() {
		// GIVEN
		Long userId = 1L;
		OrderCancelRequestDTO request = OrderCancelRequestDTO.builder()
			.orderIds(List.of(1L))
			.build();

		when(eventFacade.getUserById(any())).thenReturn(user);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(eventFacade.getTicketById(any())).thenReturn(ticket);

		// WHEN
		orderService.cancelOrder(request, userId);

		// THEN
		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
		verify(ticketQrCodeService).deleteQrCode(1L);
	}

	@Test
	@DisplayName("구매자 이메일 목록 조회")
	void getPurchaserEmails() {
		// GIVEN
		Long ticketId = 1L;
		List<String> emails = List.of("test1@example.com", "test2@example.com");

		when(orderRepository.findPurchaserEmailsByTicketId(ticketId)).thenReturn(emails);

		// WHEN
		TicketPurchaserEmailResponseDTO result = orderService.getPurchaserEmails(ticketId);

		// THEN
		assertThat(result.getEmail()).hasSize(2);
		verify(orderRepository).findPurchaserEmailsByTicketId(ticketId);
	}
} 