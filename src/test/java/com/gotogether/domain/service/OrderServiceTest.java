package com.gotogether.domain.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.order.service.OrderServiceImpl;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeService;
import com.gotogether.domain.user.entity.User;

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

	private User user;
	private Event event;
	private Ticket ticket;
	private Order order;
	private TicketQrCode ticketQrCode;
	private HostChannel hostChannel;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.email("test@example.com")
			.build();

		hostChannel = HostChannel.builder()
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
			.build();

		order = Order.builder()
			.user(user)
			.ticket(ticket)
			.status(OrderStatus.COMPLETED)
			.build();

		ticketQrCode = TicketQrCode.builder()
			.order(order)
			.qrCodeImageUrl("https://example.com/qr-code.png")
			.status(com.gotogether.domain.ticketqrcode.entity.TicketStatus.AVAILABLE)
			.build();

		order.updateTicketQrCode(ticketQrCode);
	}

	@Test
	@DisplayName("티켓 주문 생성")
	void createOrder_Success() {
		// GIVEN
		OrderRequestDTO request = OrderRequestDTO.builder()
			.ticketId(1L)
			.eventId(1L)
			.ticketCnt(2)
			.build();

		when(eventFacade.getUserById(any())).thenReturn(user);
		when(eventFacade.getTicketById(any())).thenReturn(ticket);
		when(orderRepository.save(any())).thenReturn(order);
		when(ticketQrCodeService.createQrCode(any())).thenReturn(ticketQrCode);

		// WHEN
		List<Order> orders = orderService.createOrder(request, 1L);

		// THEN
		assertThat(orders).hasSize(2);
		verify(ticketQrCodeService, times(2)).createQrCode(any());
	}

	@Test
	@DisplayName("구매한 티켓 목록 조회")
	void getPurchasedTickets_Success() {
		// GIVEN
		Pageable pageable = PageRequest.of(0, 10);
		Page<Order> orderPage = new PageImpl<>(List.of(order));
		when(eventFacade.getUserById(any())).thenReturn(user);
		when(orderRepository.findOrdersByUser(any(), any())).thenReturn(orderPage);

		// WHEN
		Page<OrderedTicketResponseDTO> result = orderService.getPurchasedTickets(1L, pageable);

		// THEN
		assertThat(result.getContent()).hasSize(1);
		verify(orderRepository).findOrdersByUser(user, pageable);
	}

	@Test
	@DisplayName("티켓 구매 확인 정보 조회")
	void getPurchaseConfirmation_Success() {
		// GIVEN
		Long userId = 1L;
		Long ticketId = 1L;
		Long eventId = 1L;

		when(eventFacade.getUserById(userId)).thenReturn(user);
		when(eventFacade.getEventById(eventId)).thenReturn(event);
		when(eventFacade.getTicketById(ticketId)).thenReturn(ticket);
		when(orderRepository.findOrderByUserAndTicket(user, ticket)).thenReturn(List.of(order));

		// WHEN
		OrderInfoResponseDTO result = orderService.getPurchaseConfirmation(userId, ticketId, eventId);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo(event.getTitle());
		assertThat(result.getTicketName()).isEqualTo(ticket.getName());
		assertThat(result.getHostChannelName()).isEqualTo(hostChannel.getName());
		assertThat(result.getOrganizerEmail()).isEqualTo(event.getOrganizerEmail());
		assertThat(result.getEventAddress()).isEqualTo(event.getAddress());
		assertThat(result.getOrderStatus()).isEqualTo(order.getStatus().name());

		verify(orderRepository).findOrderByUserAndTicket(user, ticket);
	}

	@Test
	@DisplayName("주문 취소")
	void cancelOrder_Success() {
		// GIVEN
		when(eventFacade.getUserById(any())).thenReturn(user);
		when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		when(eventFacade.getTicketById(any())).thenReturn(ticket);

		// WHEN
		orderService.cancelOrder(1L, 1L);

		// THEN
		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
		verify(ticketQrCodeService).deleteQrCode(1L);
	}

	@Test
	@DisplayName("구매자 이메일 목록 조회")
	void getPurchaserEmails_Success() {
		// GIVEN
		List<String> emails = List.of("test1@example.com", "test2@example.com");
		when(orderRepository.findPurchaserEmailsByTicketId(any())).thenReturn(emails);

		// WHEN
		TicketPurchaserEmailResponseDTO result = orderService.getPurchaserEmails(1L, 1L);

		// THEN
		assertThat(result.getEmail()).hasSize(2);
		verify(orderRepository).findPurchaserEmailsByTicketId(1L);
	}
} 