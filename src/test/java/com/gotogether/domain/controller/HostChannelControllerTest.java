package com.gotogether.domain.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.service.HostChannelService;
import com.gotogether.domain.user.dto.request.UserDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.oauth.dto.CustomOAuth2User;

@SpringBootTest
@AutoConfigureMockMvc
public class HostChannelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HostChannelService hostChannelService;

	@Autowired
	private ObjectMapper objectMapper;

	private User user;

	@BeforeEach
	void setUp() {
		UserDTO mockUserDTO = UserDTO.builder()
			.id(1L)
			.name("Test User")
			.email("test@example.com")
			.provider("google")
			.providerId("123456789")
			.build();

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(mockUserDTO);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	@Test
	void testCreateHostChannel_onSuccess() throws Exception {
		// GIVEN
		HostChannelRequestDTO request = HostChannelRequestDTO.builder()
			.hostChannelName("Test Channel")
			.channelDescription("This is a test channel")
			.build();

		HostChannel mockHostChannel = HostChannel.builder()
			.name("Test Channel")
			.description("This is a test channel")
			.build();

		when(hostChannelService.createHostChannel(anyLong(), any(HostChannelRequestDTO.class)))
			.thenReturn(mockHostChannel);

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/host-channels")
				.param("userId", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"));

		verify(hostChannelService, times(1)).createHostChannel(eq(1L), any(HostChannelRequestDTO.class));
	}

	@Test
	void testGetDetailHostChannel_onSuccess() throws Exception {
		// GIVEN
		HostChannelDetailResponseDTO responseDTO = HostChannelDetailResponseDTO.builder()
			.id(1L)
			.profileImageUrl("http://example.com/profile.jpg")
			.hostChannelName("Test Channel")
			.channelDescription("This is a test channel")
			.events(Collections.emptyList())
			.build();

		when(hostChannelService.getDetailHostChannel(anyLong()))
			.thenReturn(responseDTO);

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/{hostChannelId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result.id").value(1L))
			.andExpect(jsonPath("$.result.hostChannelName").value("Test Channel"))
			.andExpect(jsonPath("$.result.channelDescription").value("This is a test channel"))
			.andExpect(jsonPath("$.result.profileImageUrl").value("http://example.com/profile.jpg"))
			.andExpect(jsonPath("$.result.events").isArray());

		verify(hostChannelService, times(1)).getDetailHostChannel(eq(1L));
	}

	@Test
	void testUpdateHostChannel_onSuccess() throws Exception {
		// GIVEN
		HostChannelRequestDTO request = HostChannelRequestDTO.builder()
			.hostChannelName("Updated Channel")
			.channelDescription("Updated Description")
			.build();

		HostChannel updatedHostChannel = HostChannel.builder()
			.name("Updated Channel")
			.description("Updated Description")
			.build();

		when(hostChannelService.updateHostChannel(anyLong(), any(HostChannelRequestDTO.class)))
			.thenReturn(updatedHostChannel);

		// WHEN & THEN
		mockMvc.perform(put("/api/v1/host-channels/{hostChannelId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"));

		verify(hostChannelService, times(1)).updateHostChannel(eq(1L), any(HostChannelRequestDTO.class));
	}

	@Test
	void testDeleteHostChannel_onSuccess() throws Exception {
		// GIVEN
		doNothing().when(hostChannelService).deleteHostChannel(anyLong());

		// WHEN & THEN
		mockMvc.perform(delete("/api/v1/host-channels/{hostChannelId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("호스트 채널 삭제 성공"));
	}

	@Test
	void testAddMember_onSuccess() throws Exception {
		// GIVEN
		doNothing().when(hostChannelService).addMember(anyLong(), anyString());

		// WHEN & THEN
		mockMvc.perform(post("/api/v1/host-channels/{hostChannelId}/members", 1L)
				.param("email", "member@example.com"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.result").value("멤버 초대 성공"));
	}

	@Test
	void testGetMembers_onSuccess() throws Exception {
		// GIVEN
		HostChannelMemberResponseDTO member = HostChannelMemberResponseDTO.builder()
			.id(1L)
			.memberName("test")
			.build();

		when(hostChannelService.getMembers(anyLong()))
			.thenReturn(Collections.singletonList(member));

		// WHEN & THEN
		mockMvc.perform(get("/api/v1/host-channels/{hostChannelId}/members", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result[0].memberName").value("test"));
	}
}
