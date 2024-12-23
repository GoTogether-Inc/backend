package com.gotogether.domain.hostchannel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;

public interface HostChannelService {
	HostChannel createHostChannel(Long userId, HostChannelRequestDTO request);

	Page<HostChannelListResponseDTO> getHostChannels(Long userId, Pageable pageable);

}
