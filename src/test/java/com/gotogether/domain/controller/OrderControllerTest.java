package com.gotogether.domain.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.controller.OrderController;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.service.OrderService;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.interceptor.pre.TestAuthUserArgumentResolver;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	private User mockUser;
	private Ticket mockTicket;

	@BeforeEach
	void setUp() {
		HostChannel mockHostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("testchannel@example.com")
			.description("This is a test channel.")
			.profileImageUrl("http://example.com/image.png")
			.build();

		mockUser = User.builder()
			.email("test@example.com")
			.name("test")
			.phoneNumber("010-1234-5678")
			.provider("provider")
			.providerId("123456789")
			.build();

		Event mockEvent = Event.builder()
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
			.hostChannel(mockHostChannel)
			.build();

		mockTicket = Ticket.builder()
			.event(mockEvent)
			.name("Test Ticket")
			.price(10000)
			.description("This is a test ticket")
			.availableQuantity(30)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.build();

		mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
			.setCustomArgumentResolvers(new TestAuthUserArgumentResolver())
			.build();
	}

	@Test
	@DisplayName("주문 생성 성공")
	void testCreateOrder() throws Exception {
		// GIVEN
		OrderRequestDTO request = OrderRequestDTO.builder()
			.eventId(1L)
			.ticketId(1L)
			.ticketCnt(1)
			.build();

		given(orderService.createOrder(request, 1L)).willReturn(
			List.of(Order.builder()
				.status(OrderStatus.COMPLETED)
				.ticket(mockTicket)
				.user(mockUser)
				.build())
		);

		String json = new ObjectMapper().writeValueAsString(request);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/orders")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andDo(print());

		verify(orderService).createOrder(refEq(request), eq(1L));
	}

	@Test
	@DisplayName("티켓 구매 내역 조회 성공")
	void testGetPurchasedTickets() throws Exception {
		// GIVEN
		EventListResponseDTO eventResponse = EventListResponseDTO.builder()
			.id(1L)
			.bannerImageUrl("https://example.com/banner.jpg")
			.title("Test Event")
			.hostChannelName("Test Channel")
			.startDate("2025-05-10")
			.address("Test Address")
			.onlineType("ONLINE")
			.hashtags(List.of("DEVELOP", "CONFERENCE"))
			.remainDays("D-5")
			.build();

		OrderedTicketResponseDTO response = OrderedTicketResponseDTO.builder()
			.id(1L)
			.event(eventResponse)
			.ticketQrCode("QR123")
			.ticketName("VIP Ticket")
			.ticketPrice(50000)
			.orderStatus("COMPLETED")
			.isCheckIn(false)
			.build();

		Page<OrderedTicketResponseDTO> page = new PageImpl<>(List.of(response));

		given(orderService.getPurchasedTickets(1L, PageRequest.of(0, 10)))
			.willReturn(page);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/orders")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].event.title").value("Test Event"))
			.andExpect(jsonPath("$.result[0].ticketQrCode").value("QR123"))
			.andExpect(jsonPath("$.result[0].ticketName").value("VIP Ticket"))
			.andExpect(jsonPath("$.result[0].ticketPrice").value(50000))
			.andExpect(jsonPath("$.result[0].orderStatus").value("COMPLETED"))
			.andExpect(jsonPath("$.result[0].checkIn").value(false))
			.andDo(print());

		verify(orderService).getPurchasedTickets(1L, PageRequest.of(0, 10));
	}

	@Test
	@DisplayName("주문 확인 조회 성공")
	void testGetPurchaseConfirmation() throws Exception {
		// GIVEN
		OrderInfoResponseDTO response = OrderInfoResponseDTO.builder()
			.id(1L)
			.title("Test Event")
			.startDate("2025-05-20")
			.startTime("18:30")
			.ticketName("VIP Ticket")
			.hostChannelName("Test Channel")
			.hostChannelDescription("This is a test channel.")
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.eventAddress("Test Address")
			.location(Map.of("latitude", 37.5499, "longitude", 126.9136))
			.orderStatus("COMPLETED")
			.build();

		given(orderService.getPurchaseConfirmation(1L, 1L, 1L)).willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/orders/purchase-confirmation")
				.param("ticketId", "1")
				.param("eventId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.title").value("Test Event"))
			.andExpect(jsonPath("$.result.startDate").value("2025-05-20"))
			.andExpect(jsonPath("$.result.startTime").value("18:30"))
			.andExpect(jsonPath("$.result.ticketName").value("VIP Ticket"))
			.andExpect(jsonPath("$.result.hostChannelName").value("Test Channel"))
			.andExpect(jsonPath("$.result.hostChannelDescription").value("This is a test channel."))
			.andDo(print());

		verify(orderService).getPurchaseConfirmation(1L, 1L, 1L);
	}

	@Test
	@DisplayName("주문 취소 성공")
	void testCancelOrder() throws Exception {
		// WHEN & THEN
		mockMvc.perform(post("/api/v1/orders/cancel")
				.param("orderId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result").value("주문 취소 성공"))
			.andDo(print());

		verify(orderService).cancelOrder(1L, 1L);
	}
}
