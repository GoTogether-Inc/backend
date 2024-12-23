package com.gotogether.domain.hostchannel.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
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
}
