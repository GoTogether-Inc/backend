package com.gotogether.domain.hostchannel;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.channelorganizer.repository.ChannelOrganizerRepository;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.entity.HostChannelStatus;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.hostchannel.service.HostChannelServiceImpl;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderCustomRepository;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.entity.TicketStatus;
import com.gotogether.domain.ticket.entity.TicketType;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeService;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.common.service.S3UploadService;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class HostChannelServiceTest {

	@InjectMocks
	private HostChannelServiceImpl hostChannelService;

	@Mock
	private HostChannelRepository hostChannelRepository;

	@Mock
	private ChannelOrganizerRepository channelOrganizerRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderCustomRepository orderCustomRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private TicketQrCodeService ticketQrCodeService;

	@Mock
	private S3UploadService s3UploadService;

	@Mock
	private MetricService metricService;

	@Mock
	private EventFacade eventFacade;

	private User user;
	private HostChannel hostChannel;
	private Event event;
	private Ticket ticket;
	private Order order;
	private ChannelOrganizer channelOrganizer;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.name("Test User")
			.email("test@example.com")
			.provider("google")
			.providerId("123")
			.build();

		hostChannel = HostChannel.builder()
			.name("Test Channel")
			.email("test@example.com")
			.description("Test Description")
			.profileImageUrl("http://example.com/image.jpg")
			.build();

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

		ticket = Ticket.builder()
			.name("Test Ticket")
			.price(10000)
			.availableQuantity(50)
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusDays(1))
			.type(TicketType.FIRST_COME)
			.status(TicketStatus.OPEN)
			.event(event)
			.build();

		order = Order.builder()
			.orderCode("TEST-ORDER-001")
			.status(OrderStatus.PENDING)
			.user(user)
			.ticket(ticket)
			.build();

		channelOrganizer = ChannelOrganizer.builder()
			.user(user)
			.hostChannel(hostChannel)
			.build();
	}

	@Test
	@DisplayName("호스트 채널 생성")
	void createHostChannelNewChannel() {
		// GIVEN
		Long userId = 1L;
		HostChannelRequestDTO request = HostChannelRequestDTO.builder()
			.hostChannelName("New Channel")
			.hostEmail("new@example.com")
			.channelDescription("New Description")
			.profileImageUrl("http://example.com/new-image.jpg")
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(hostChannelRepository.findByName(request.getHostChannelName())).thenReturn(Optional.empty());
		when(hostChannelRepository.save(any(HostChannel.class))).thenReturn(hostChannel);
		when(channelOrganizerRepository.save(any(ChannelOrganizer.class))).thenReturn(channelOrganizer);
		when(s3UploadService.moveTempImageToFinal(anyString())).thenReturn("http://example.com/final-image.jpg");

		// WHEN
		HostChannel result = hostChannelService.createHostChannel(userId, request);

		// THEN
		assertThat(result).isNotNull();
		verify(userRepository).findById(userId);
		verify(hostChannelRepository).findByName(request.getHostChannelName());
		verify(hostChannelRepository).save(any(HostChannel.class));
		verify(channelOrganizerRepository).save(any(ChannelOrganizer.class));
		verify(metricService).recordHostChannelCreation(any());
	}

	@Test
	@DisplayName("사용자 호스트 채널 목록 조회")
	void getHostChannels() {
		// GIVEN
		Long userId = 1L;
		List<HostChannel> hostChannels = Collections.singletonList(hostChannel);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(hostChannelRepository.findActiveHostChannelsByUser(user, HostChannelStatus.INACTIVE))
			.thenReturn(hostChannels);

		// WHEN
		List<HostChannelListResponseDTO> result = hostChannelService.getHostChannels(userId);

		// THEN
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getHostChannelName()).isEqualTo("Test Channel");
		verify(userRepository).findById(userId);
		verify(hostChannelRepository).findActiveHostChannelsByUser(user, HostChannelStatus.INACTIVE);
	}

	@Test
	@DisplayName("호스트 채널 상세 조회")
	void getDetailHostChannel() {
		// GIVEN
		Long hostChannelId = 1L;
		List<Event> events = Collections.singletonList(event);

		when(hostChannelRepository.findById(hostChannelId)).thenReturn(Optional.of(hostChannel));
		when(eventRepository.findAllByHostChannelId(hostChannelId)).thenReturn(events);

		// WHEN
		HostChannelDetailResponseDTO result = hostChannelService.getDetailHostChannel(hostChannelId);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getHostChannelName()).isEqualTo("Test Channel");
		verify(hostChannelRepository).findById(hostChannelId);
		verify(eventRepository).findAllByHostChannelId(hostChannelId);
	}

	@Test
	@DisplayName("호스트 채널 삭제")
	void deleteHostChannel() {
		// GIVEN
		Long hostChannelId = 1L;

		when(eventFacade.getHostChannelById(hostChannelId)).thenReturn(hostChannel);
		when(eventRepository.countByHostChannel(hostChannel)).thenReturn(0L);
		when(channelOrganizerRepository.countByHostChannel(hostChannel)).thenReturn(1L);

		// WHEN
		hostChannelService.deleteHostChannel(hostChannelId);

		// THEN
		verify(eventFacade).getHostChannelById(hostChannelId);
		verify(eventRepository).countByHostChannel(hostChannel);
		verify(channelOrganizerRepository).countByHostChannel(hostChannel);
	}

	@Test
	@DisplayName("호스트 채널 수정")
	void updateHostChannel() {
		// GIVEN
		Long hostChannelId = 1L;
		HostChannelRequestDTO request = HostChannelRequestDTO.builder()
			.hostChannelName("Updated Channel")
			.hostEmail("updated@example.com")
			.channelDescription("Updated Description")
			.profileImageUrl("http://example.com/updated-image.jpg")
			.build();

		when(eventFacade.getHostChannelById(hostChannelId)).thenReturn(hostChannel);
		when(hostChannelRepository.save(hostChannel)).thenReturn(hostChannel);
		when(s3UploadService.moveTempImageToFinal(request.getProfileImageUrl())).thenReturn(
			"http://example.com/final-image.jpg");

		// WHEN
		HostChannel result = hostChannelService.updateHostChannel(hostChannelId, request);

		// THEN
		assertThat(result).isNotNull();
		verify(eventFacade).getHostChannelById(hostChannelId);
		verify(s3UploadService).deleteFile(anyString());
		verify(s3UploadService).moveTempImageToFinal(request.getProfileImageUrl());
		verify(hostChannelRepository).save(hostChannel);
	}

	@Test
	@DisplayName("호스트 채널 멤버 추가")
	void addMember() {
		// GIVEN
		Long hostChannelId = 1L;
		String email = "newmember@example.com";
		User newUser = User.builder()
			.name("New Member")
			.email(email)
			.provider("google")
			.providerId("456")
			.build();

		when(eventFacade.getHostChannelById(hostChannelId)).thenReturn(hostChannel);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(newUser));
		when(channelOrganizerRepository.existsByUserAndHostChannel(newUser, hostChannel)).thenReturn(false);
		when(channelOrganizerRepository.save(any(ChannelOrganizer.class))).thenReturn(channelOrganizer);

		// WHEN
		hostChannelService.addMember(hostChannelId, email);

		// THEN
		verify(eventFacade).getHostChannelById(hostChannelId);
		verify(userRepository).findByEmail(email);
		verify(channelOrganizerRepository).existsByUserAndHostChannel(newUser, hostChannel);
		verify(channelOrganizerRepository).save(any(ChannelOrganizer.class));
	}

	@Test
	@DisplayName("호스트 채널 멤버 조회")
	void getMembers() {
		// GIVEN
		Long hostChannelId = 1L;

		when(eventFacade.getHostChannelById(hostChannelId)).thenReturn(hostChannel);
		when(channelOrganizerRepository.findChannelOrganizerWithUserByHostChannel(hostChannel))
			.thenReturn(Collections.singletonList(channelOrganizer));

		// WHEN
		List<HostChannelMemberResponseDTO> result = hostChannelService.getMembers(hostChannelId);

		// THEN
		assertThat(result).hasSize(1);
		verify(eventFacade).getHostChannelById(hostChannelId);
		verify(channelOrganizerRepository).findChannelOrganizerWithUserByHostChannel(hostChannel);
	}

	@Test
	@DisplayName("호스트 대시보드 조회")
	void getHostDashboard() {
		// GIVEN
		Long eventId = 1L;
		List<Order> orders = Collections.singletonList(order);

		when(eventFacade.getEventById(eventId)).thenReturn(event);
		when(orderRepository.findCompletedOrdersByEventId(eventId, OrderStatus.COMPLETED)).thenReturn(orders);

		// WHEN
		HostDashboardResponseDTO result = hostChannelService.getHostDashboard(eventId);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getTotalTicketCnt()).isEqualTo(1L);
		assertThat(result.getTotalPrice()).isEqualTo(10000L);
		verify(eventFacade).getEventById(eventId);
		verify(orderRepository).findCompletedOrdersByEventId(eventId, OrderStatus.COMPLETED);
	}

	@Test
	@DisplayName("주문 승인")
	void approveOrderStatusOffline() {
		// GIVEN
		Long orderId = 1L;
		TicketQrCode qrCode = TicketQrCode.builder()
			.order(order)
			.build();

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(ticketQrCodeService.createQrCode(order)).thenReturn(qrCode);
		when(orderRepository.save(order)).thenReturn(order);

		// WHEN
		hostChannelService.approveOrderStatus(orderId);

		// THEN
		verify(orderRepository).findById(orderId);
		verify(ticketQrCodeService).createQrCode(order);
		verify(orderRepository).save(order);
	}

	@Test
	@DisplayName("호스트 채널 정보 조회")
	void getHostChannelInfo() {
		// GIVEN
		Long hostChannelId = 1L;

		when(eventFacade.getHostChannelById(hostChannelId)).thenReturn(hostChannel);
		when(channelOrganizerRepository.findChannelOrganizerWithUserByHostChannel(hostChannel))
			.thenReturn(Collections.singletonList(channelOrganizer));

		// WHEN
		HostChannelInfoResponseDTO result = hostChannelService.getHostChannelInfo(hostChannelId);

		// THEN
		assertThat(result).isNotNull();
		verify(eventFacade, times(2)).getHostChannelById(hostChannelId); // getHostChannelInfo + getMembers에서 호출
		verify(channelOrganizerRepository).findChannelOrganizerWithUserByHostChannel(hostChannel);
	}

	@Test
	@DisplayName("호스트 채널 검색")
	void searchHostChannels() {
		// GIVEN
		String keyword = "Test";
		Pageable pageable = PageRequest.of(0, 10);
		Page<HostChannel> hostChannelPage = new PageImpl<>(Collections.singletonList(hostChannel));

		when(hostChannelRepository.findHostChannelByFilter(keyword, pageable)).thenReturn(hostChannelPage);

		// WHEN
		Page<HostChannelListResponseDTO> result = hostChannelService.searchHostChannels(keyword, pageable);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		verify(hostChannelRepository).findHostChannelByFilter(keyword, pageable);
	}

	@Test
	@DisplayName("구매/참가자 관리 조회")
	void getParticipantManagement() {
		// GIVEN
		Long eventId = 1L;
		String tag = "approved";
		Pageable pageable = PageRequest.of(0, 10);

		List<Ticket> tickets = Collections.singletonList(ticket);
		Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));

		when(ticketRepository.findByEventId(eventId)).thenReturn(tickets);
		when(orderCustomRepository.findByTicketIdsAndStatus(anyList(), eq(tag), eq(pageable)))
			.thenReturn(orderPage);

		// WHEN
		List<ParticipantManagementResponseDTO> result = hostChannelService.getParticipantManagement(eventId, tag,
			pageable);

		// THEN
		assertThat(result).hasSize(1);
		verify(ticketRepository).findByEventId(eventId);
		verify(orderCustomRepository).findByTicketIdsAndStatus(anyList(), eq(tag), eq(pageable));
	}
} 