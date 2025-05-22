package com.gotogether.domain.hostchannel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.request.InviteMemberRequest;
import com.gotogether.domain.hostchannel.dto.request.OrderStatusRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.service.HostChannelService;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/host-channels")
public class HostChannelController {

	private final HostChannelService hostChannelService;

	@PostMapping
	public ApiResponse<?> createEvent(
		@AuthUser Long userId,
		@RequestBody @Valid HostChannelRequestDTO request) {
		HostChannel hostChannel = hostChannelService.createHostChannel(userId, request);
		return ApiResponse.onSuccessCreated(hostChannel.getId());
	}

	@GetMapping
	public ApiResponse<List<HostChannelListResponseDTO>> getHostChannelsByUser(
		@AuthUser Long userId) {
		List<HostChannelListResponseDTO> hostChannels = hostChannelService.getHostChannels(userId);
		return ApiResponse.onSuccess(hostChannels);
	}

	@GetMapping("/{hostChannelId}")
	public ApiResponse<HostChannelDetailResponseDTO> getDetailHostChannel(
		@PathVariable Long hostChannelId) {
		return ApiResponse.onSuccess(hostChannelService.getDetailHostChannel(hostChannelId));
	}

	@GetMapping("/{hostChannelId}/info")
	public ApiResponse<HostChannelInfoResponseDTO> getHostChannelInfo(
		@PathVariable Long hostChannelId) {
		return ApiResponse.onSuccess(hostChannelService.getHostChannelInfo(hostChannelId));
	}

	@GetMapping("/search")
	public ApiResponse<List<HostChannelListResponseDTO>> getHostChannelsSearch(
		@RequestParam(required = false) String keyword,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<HostChannelListResponseDTO> hostChannels = hostChannelService.searchHostChannels(keyword, pageable);
		return ApiResponse.onSuccess(hostChannels.getContent());
	}

	@PutMapping("/{hostChannelId}")
	public ApiResponse<?> updateHostChannel(
		@PathVariable Long hostChannelId,
		@RequestBody @Valid HostChannelRequestDTO request) {
		HostChannel hostChannel = hostChannelService.updateHostChannel(hostChannelId, request);
		return ApiResponse.onSuccess(hostChannel.getId());
	}

	@DeleteMapping("/{hostChannelId}")
	public ApiResponse<?> deleteHostChannel(
		@PathVariable Long hostChannelId) {
		hostChannelService.deleteHostChannel(hostChannelId);
		return ApiResponse.onSuccess("호스트 채널 삭제 성공");
	}

	@PostMapping("/{hostChannelId}/members")
	public ApiResponse<?> addMember(
		@PathVariable Long hostChannelId,
		@RequestBody @Valid InviteMemberRequest request) {
		hostChannelService.addMember(hostChannelId, request.getEmail());
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

	@GetMapping("/dashboard")
	public ApiResponse<HostDashboardResponseDTO> getHostDashboard(
		@RequestParam Long eventId) {
		return ApiResponse.onSuccess(hostChannelService.getHostDashboard(eventId));
	}

	@GetMapping("/dashboard/participant-management")
	public ApiResponse<List<ParticipantManagementResponseDTO>> getParticipantManagement(
		@RequestParam Long eventId,
		@RequestParam(name = "tag", defaultValue = "all") String tag,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ApiResponse.onSuccess(hostChannelService.getParticipantManagement(eventId, tag, pageable));
	}

	@PatchMapping("/dashboard/participant-management/approve")
	public ApiResponse<?> approveOrderStatus(
		@RequestBody @Valid OrderStatusRequestDTO request) {
		hostChannelService.approveOrderStatus(request.getOrderId());
		return ApiResponse.onSuccess("주문 승인 완료");
	}
}
