package com.gotogether.domain.hostchannel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.service.HostChannelService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/host-channels")
public class HostChannelController {

	private final HostChannelService hostChannelService;

	@PostMapping
	public ApiResponse<?> createEvent(@RequestParam(value = "userId") Long userId,
		@RequestBody HostChannelRequestDTO request) {
		HostChannel hostChannel = hostChannelService.createHostChannel(userId, request);
		return ApiResponse.onSuccess("hostChannelId: " + hostChannel.getId());
	}

	@GetMapping
	public ApiResponse<List<HostChannelListResponseDTO>> getHostChannels(
		@RequestParam(value = "userId") Long userId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<HostChannelListResponseDTO> hostChannels = hostChannelService.getHostChannels(userId, pageable);
		return ApiResponse.onSuccess(hostChannels.getContent());
	}
}
