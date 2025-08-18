package com.gotogether.domain.hostchannel;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.request.InviteMemberRequestDTO;
import com.gotogether.domain.hostchannel.dto.request.OrderStatusRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.service.HostChannelService;
import com.gotogether.global.util.TestUserUtil;
import com.gotogether.global.util.TestUserUtil.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class HostChannelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HostChannelService hostChannelService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestUserUtil testUserUtil;

	private TestUser testUser;

	@BeforeEach
	void setUp() {
		testUser = testUserUtil.createTestUser();
	}

	@Test
	@DisplayName("호스트 채널 생성")
	void createHostChannel() throws Exception {
		// GIVEN
		HostChannelRequestDTO request = HostChannelRequestDTO.builder()
			.hostChannelName("Test Channel")
			.channelDescription("This is a test channel")
			.profileImageUrl("http://example.com/profile.jpg")
			.hostEmail("test@example.com")
			.build();

		HostChannel mockHostChannel = HostChannel.builder()
			.name("Test Channel")
			.description("This is a test channel")
			.profileImageUrl("http://example.com/profile.jpg")
			.email("test@example.com")
			.build();

		given(hostChannelService.createHostChannel(any(Long.class), any(HostChannelRequestDTO.class)))
			.willReturn(mockHostChannel);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/host-channels")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andDo(print());

		verify(hostChannelService).createHostChannel(any(Long.class), any(HostChannelRequestDTO.class));
	}

	@Test
	@DisplayName("호스트 채널 상세 조회")
	void getDetailHostChannel() throws Exception {
		// GIVEN
		HostChannelDetailResponseDTO response = HostChannelDetailResponseDTO.builder()
			.id(1L)
			.profileImageUrl("http://example.com/profile.jpg")
			.hostChannelName("Test Channel")
			.channelDescription("This is a test channel")
			.events(Collections.emptyList())
			.build();

		given(hostChannelService.getDetailHostChannel(anyLong()))
			.willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/{hostChannelId}", 1L)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.id").value(1L))
			.andExpect(jsonPath("$.result.hostChannelName").value("Test Channel"))
			.andExpect(jsonPath("$.result.channelDescription").value("This is a test channel"))
			.andExpect(jsonPath("$.result.profileImageUrl").value("http://example.com/profile.jpg"))
			.andExpect(jsonPath("$.result.events").isArray())
			.andDo(print());

		verify(hostChannelService).getDetailHostChannel(eq(1L));
	}

	@Test
	@DisplayName("호스트 채널 수정")
	void updateHostChannel() throws Exception {
		// GIVEN
		HostChannelRequestDTO request = HostChannelRequestDTO.builder()
			.hostChannelName("Updated Channel")
			.channelDescription("Updated Description")
			.profileImageUrl("http://example.com/updated-profile.jpg")
			.hostEmail("test@example.com")
			.build();

		HostChannel updatedHostChannel = HostChannel.builder()
			.name("Updated Channel")
			.description("Updated Description")
			.profileImageUrl("http://example.com/updated-profile.jpg")
			.email("test@example.com")
			.build();

		given(hostChannelService.updateHostChannel(eq(1L), any(HostChannelRequestDTO.class)))
			.willReturn(updatedHostChannel);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/host-channels/{hostChannelId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andDo(print());

		verify(hostChannelService).updateHostChannel(eq(1L), any(HostChannelRequestDTO.class));
	}

	@Test
	@DisplayName("호스트 채널 삭제")
	void deleteHostChannel() throws Exception {
		// GIVEN
		willDoNothing().given(hostChannelService).deleteHostChannel(anyLong());

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/host-channels/{hostChannelId}", 1L)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("호스트 채널 삭제 성공"))
			.andDo(print());

		verify(hostChannelService).deleteHostChannel(eq(1L));
	}

	@Test
	@DisplayName("호스트 채널 멤버 추가")
	void addMember() throws Exception {
		// GIVEN
		InviteMemberRequestDTO request = InviteMemberRequestDTO.builder()
			.email("member@example.com")
			.build();

		willDoNothing().given(hostChannelService).addMember(anyLong(), anyString());

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/host-channels/{hostChannelId}/members", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("멤버 초대 성공"))
			.andDo(print());

		verify(hostChannelService).addMember(eq(1L), eq("member@example.com"));
	}

	@Test
	@DisplayName("호스트 채널 멤버 조회")
	void getMembers() throws Exception {
		// GIVEN
		HostChannelMemberResponseDTO member = HostChannelMemberResponseDTO.builder()
			.id(1L)
			.memberName("test")
			.build();

		given(hostChannelService.getMembers(anyLong()))
			.willReturn(Collections.singletonList(member));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/{hostChannelId}/members", 1L)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].memberName").value("test"))
			.andDo(print());

		verify(hostChannelService).getMembers(eq(1L));
	}

	@Test
	@DisplayName("호스트 채널 목록 조회")
	void getHostChannelsByUser() throws Exception {
		// GIVEN
		HostChannelListResponseDTO response = HostChannelListResponseDTO.builder()
			.id(1L)
			.profileImageUrl("http://example.com/profile.jpg")
			.hostChannelName("Test Channel")
			.build();

		given(hostChannelService.getHostChannels(any(Long.class)))
			.willReturn(List.of(response));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].hostChannelName").value("Test Channel"))
			.andDo(print());

		verify(hostChannelService).getHostChannels(any(Long.class));
	}

	@Test
	@DisplayName("호스트 채널 정보 조회")
	void getHostChannelInfo() throws Exception {
		// GIVEN
		HostChannelInfoResponseDTO response = HostChannelInfoResponseDTO.builder()
			.id(1L)
			.profileImageUrl("http://example.com/profile.jpg")
			.hostChannelName("Test Channel")
			.channelDescription("This is a test channel")
			.email("test@example.com")
			.hostChannelMembers(Collections.emptyList())
			.build();

		given(hostChannelService.getHostChannelInfo(eq(1L)))
			.willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/{hostChannelId}/info", 1L)
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.hostChannelName").value("Test Channel"))
			.andExpect(jsonPath("$.result.channelDescription").value("This is a test channel"))
			.andDo(print());

		verify(hostChannelService).getHostChannelInfo(eq(1L));
	}

	@Test
	@DisplayName("호스트 채널 검색")
	void getHostChannelsSearch() throws Exception {
		// GIVEN
		HostChannelListResponseDTO response = HostChannelListResponseDTO.builder()
			.id(1L)
			.profileImageUrl("http://example.com/profile.jpg")
			.hostChannelName("Test Channel")
			.build();

		Page<HostChannelListResponseDTO> page = new PageImpl<>(List.of(response));
		given(hostChannelService.searchHostChannels(anyString(), any(Pageable.class)))
			.willReturn(page);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/search")
				.param("keyword", "test")
				.param("page", "0")
				.param("size", "10")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andDo(print());

		verify(hostChannelService).searchHostChannels(eq("test"), any(Pageable.class));
	}

	@Test
	@DisplayName("호스트 대시보드 조회")
	void getHostDashboard() throws Exception {
		// GIVEN
		HostDashboardResponseDTO response = HostDashboardResponseDTO.builder()
			.eventName("Test Event")
			.isTicket(true)
			.isTicketOption(true)
			.eventStartDate("2024-03-20")
			.eventEndDate("2024-03-20")
			.totalTicketCnt(100L)
			.totalPrice(1000000L)
			.build();

		given(hostChannelService.getHostDashboard(eq(1L)))
			.willReturn(response);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/dashboard")
				.param("eventId", "1")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.eventName").value("Test Event"))
			.andExpect(jsonPath("$.result.totalTicketCnt").value(100))
			.andDo(print());

		verify(hostChannelService).getHostDashboard(eq(1L));
	}

	@Test
	@DisplayName("구매/참가자 관리 조회")
	void getParticipantManagement() throws Exception {
		// GIVEN
		ParticipantManagementResponseDTO response = ParticipantManagementResponseDTO.builder()
			.id(1L)
			.ticketId(1L)
			.orderCode("ORDER123")
			.participant("Test User")
			.email("test@example.com")
			.phoneNumber("010-1234-5678")
			.purchaseDate("2024-03-20")
			.ticketName("VIP Ticket")
			.ticketType("FIRST_COME")
			.isCheckedIn(false)
			.isApproved(false)
			.build();

		given(hostChannelService.getParticipantManagement(eq(1L), eq("all"), any(Pageable.class)))
			.willReturn(Collections.singletonList(response));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/dashboard/participant-management")
				.param("eventId", "1")
				.param("tag", "all")
				.param("page", "0")
				.param("size", "10")
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].id").value(1L))
			.andExpect(jsonPath("$.result[0].ticketId").value(1L))
			.andExpect(jsonPath("$.result[0].orderCode").value("ORDER123"))
			.andExpect(jsonPath("$.result[0].participant").value("Test User"))
			.andExpect(jsonPath("$.result[0].email").value("test@example.com"))
			.andExpect(jsonPath("$.result[0].phoneNumber").value("010-1234-5678"))
			.andExpect(jsonPath("$.result[0].purchaseDate").value("2024-03-20"))
			.andExpect(jsonPath("$.result[0].ticketName").value("VIP Ticket"))
			.andExpect(jsonPath("$.result[0].ticketType").value("FIRST_COME"))
			.andExpect(jsonPath("$.result[0].checkedIn").value(false))
			.andExpect(jsonPath("$.result[0].approved").value(false))
			.andDo(print());

		verify(hostChannelService).getParticipantManagement(eq(1L), eq("all"), any(Pageable.class));
	}

	@Test
	@DisplayName("주문 승인")
	void approveOrderStatus() throws Exception {
		// GIVEN
		OrderStatusRequestDTO request = OrderStatusRequestDTO.builder()
			.orderId(1L)
			.build();

		willDoNothing().given(hostChannelService).approveOrderStatus(eq(1L));

		// WHEN & THEN
		mockMvc.perform(patch("/api/v1/host-channels/dashboard/participant-management/approve")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.cookie(testUser.accessTokenCookie(), testUser.refreshTokenCookie()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result").value("주문 승인 완료"))
			.andDo(print());

		verify(hostChannelService).approveOrderStatus(eq(1L));
	}
}
