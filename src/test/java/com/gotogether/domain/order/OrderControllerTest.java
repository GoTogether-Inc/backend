package com.gotogether.domain.order;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.dto.request.OrderCancelRequestDTO;
import com.gotogether.domain.order.dto.request.OrderRequestDTO;
import com.gotogether.domain.order.dto.response.OrderInfoResponseDTO;
import com.gotogether.domain.order.dto.response.OrderedTicketResponseDTO;
import com.gotogether.domain.order.dto.response.TicketPurchaserEmailResponseDTO;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.service.OrderService;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;
	private Ticket mockTicket;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();

		HostChannel mockHostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("testchannel@example.com")
			.description("This is a test channel.")
			.profileImageUrl("http://example.com/image.png")
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
	}

	@Test
	@DisplayName("주문 생성")
	void createOrder() throws Exception {
		// GIVEN
		OrderRequestDTO request = OrderRequestDTO.builder()
			.eventId(1L)
			.ticketId(1L)
			.ticketCnt(1)
			.build();

		Order mockOrder = Order.builder()
			.status(OrderStatus.COMPLETED)
			.ticket(mockTicket)
			.user(testUser.user())
			.build();

		ReflectionTestUtils.setField(mockOrder, "id", 1L);

		given(orderService.createOrder(any(OrderRequestDTO.class), eq(testUser.user().getId())))
			.willReturn(List.of(mockOrder));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.result[0]").value(1L))
			.andDo(print());

		verify(orderService).createOrder(refEq(request), eq(testUser.user().getId()));
	}

	@Test
	@DisplayName("티켓 구매 내역 조회")
	void getPurchasedTickets() throws Exception {
		// GIVEN
		OrderedTicketResponseDTO response = OrderedTicketResponseDTO.builder()
			.orderId(1L)
			.event(EventListResponseDTO.builder()
				.id(1L)
				.bannerImageUrl("https://example.com/banner.jpg")
				.title("Test Event")
				.hostChannelName("Test Channel")
				.startDate(String.valueOf(LocalDateTime.now()))
				.address("Test Address")
				.onlineType("ONLINE")
				.hashtags(List.of("DEVELOP", "CONFERENCE"))
				.remainDays("D-5")
				.build())
			.ticketQrCode("QR123")
			.ticketName("Test Ticket")
			.ticketPrice(10000)
			.orderStatus("COMPLETED")
			.isCheckIn(false)
			.build();

		given(orderService.getPurchasedTickets(eq(testUser.user().getId())))
			.willReturn(List.of(response));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/orders")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].orderId").value(1L))
			.andExpect(jsonPath("$.result[0].event.title").value("Test Event"))
			.andExpect(jsonPath("$.result[0].ticketQrCode").value("QR123"))
			.andExpect(jsonPath("$.result[0].ticketName").value("Test Ticket"))
			.andExpect(jsonPath("$.result[0].ticketPrice").value(10000))
			.andExpect(jsonPath("$.result[0].orderStatus").value("COMPLETED"))
			.andExpect(jsonPath("$.result[0].checkIn").value(false))
			.andDo(print());

		verify(orderService).getPurchasedTickets(eq(testUser.user().getId()));
	}

	@Test
	@DisplayName("주문 확인 조회")
	void getPurchaseConfirmation() throws Exception {
		// GIVEN
		Long orderId = 1L;
		OrderInfoResponseDTO response = OrderInfoResponseDTO.builder()
			.id(orderId)
			.title("Test Event")
			.startDate(String.valueOf(LocalDateTime.now()))
			.ticketName("Test Ticket")
			.hostChannelName("Test Channel")
			.hostChannelDescription("This is a test channel.")
			.organizerEmail("test@example.com")
			.organizerPhoneNumber("010-1234-5678")
			.eventAddress("Test Location")
			.locationLat(100.0)
			.locationLng(200.0)
			.orderStatus("COMPLETED")
			.build();

		given(orderService.getPurchaseConfirmation(eq(orderId)))
			.willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/orders/{orderId}/purchase-confirmation", orderId)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("OK"))
			.andExpect(jsonPath("$.result.id").value(1))
			.andExpect(jsonPath("$.result.title").value("Test Event"))
			.andExpect(jsonPath("$.result.ticketName").value("Test Ticket"))
			.andExpect(jsonPath("$.result.hostChannelName").value("Test Channel"))
			.andExpect(jsonPath("$.result.hostChannelDescription").value("This is a test channel."))
			.andDo(print());

		verify(orderService).getPurchaseConfirmation(eq(orderId));
	}

	@Test
	@DisplayName("주문 취소")
	void cancelOrder() throws Exception {
		// GIVEN
		Long userId = testUser.user().getId();
		OrderCancelRequestDTO request = OrderCancelRequestDTO.builder()
			.orderIds(List.of(1L, 2L))
			.build();

		willDoNothing().given(orderService).cancelOrder(any(OrderCancelRequestDTO.class), eq(userId));

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/orders/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("주문 취소 성공"))
			.andDo(print());

		verify(orderService).cancelOrder(any(OrderCancelRequestDTO.class), eq(userId));
	}

	@Test
	@DisplayName("구매자 이메일 조회")
	void getPurchaserEmails() throws Exception {
		// GIVEN
		Long ticketId = 1L;
		TicketPurchaserEmailResponseDTO response = TicketPurchaserEmailResponseDTO.builder()
			.email(List.of("test1@example.com", "test2@example.com"))
			.build();

		given(orderService.getPurchaserEmails(eq(ticketId)))
			.willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/orders/purchaser-emails")
				.param("ticketId", String.valueOf(ticketId))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.email[0]").value("test1@example.com"))
			.andExpect(jsonPath("$.result.email[1]").value("test2@example.com"))
			.andDo(print());

		verify(orderService).getPurchaserEmails(eq(ticketId));
	}
}
