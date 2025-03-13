package com.gotogether.domain.hostchannel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.service.HostChannelService;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/host-channels")
public class HostChannelController {

	private final HostChannelService hostChannelService;

	@PostMapping
	public ApiResponse<?> createEvent(@AuthUser Long userId,
		@RequestBody HostChannelRequestDTO request) {
		HostChannel hostChannel = hostChannelService.createHostChannel(userId, request);
		return ApiResponse.onSuccessCreated("hostChannelId: " + hostChannel.getId());
	}

	@GetMapping
	public ApiResponse<List<HostChannelListResponseDTO>> getHostChannels(
		@AuthUser Long userId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<HostChannelListResponseDTO> hostChannels = hostChannelService.getHostChannels(userId, pageable);
		return ApiResponse.onSuccess(hostChannels.getContent());
	}

	@GetMapping("/{hostChannelId}")
	public ApiResponse<HostChannelDetailResponseDTO> getDetailHostChannel(@PathVariable Long hostChannelId) {
		return ApiResponse.onSuccess(hostChannelService.getDetailHostChannel(hostChannelId));
	}

	@PutMapping("/{hostChannelId}")
	public ApiResponse<?> updateHostChannel(@PathVariable Long hostChannelId,
		@RequestBody HostChannelRequestDTO request) {
		HostChannel hostChannel = hostChannelService.updateHostChannel(hostChannelId, request);
		return ApiResponse.onSuccess("hostChannelId: " + hostChannel.getId());
	}

	@DeleteMapping("/{hostChannelId}")
	public ApiResponse<?> deleteHostChannel(@PathVariable Long hostChannelId) {
		hostChannelService.deleteHostChannel(hostChannelId);
		return ApiResponse.onSuccess("호스트 채널 삭제 성공");
	}

	@PostMapping("/{hostChannelId}/members")
	public ApiResponse<?> addMember(@PathVariable Long hostChannelId,
		@RequestParam(value = "email") String email) {
		hostChannelService.addMember(hostChannelId, email);
		return ApiResponse.onSuccess("멤버 초대 성공");
	}

	@GetMapping("/{hostChannelId}/members")
	public ApiResponse<List<HostChannelMemberResponseDTO>> getMembers(
		@PathVariable Long hostChannelId) {
		return ApiResponse.onSuccess(hostChannelService.getMembers(hostChannelId));
	}

	/*
	 * 컨트롤러 분리하기
	 */

	@GetMapping("/{hostChannelId}/dashboard")
	public ApiResponse<HostDashboardResponseDTO> getHostDashboard(@PathVariable Long hostChannelId,
		@RequestParam Long eventId) {
		return ApiResponse.onSuccess(hostChannelService.getHostDashboard(eventId));
	}

	@GetMapping("/dashboard/participant-management")
	public ApiResponse<List<ParticipantManagementResponseDTO>> getParticipantManagement(
		@RequestParam Long eventId,
		@RequestParam(name = "tags", defaultValue = "all") String tags,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ApiResponse.onSuccess(hostChannelService.getParticipantManagement(eventId, tags, pageable));
	}
}
