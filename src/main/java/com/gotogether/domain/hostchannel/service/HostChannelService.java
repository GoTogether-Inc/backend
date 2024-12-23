package com.gotogether.domain.hostchannel.service;

import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;

public interface HostChannelService {
	HostChannel createHostChannel(Long userId, HostChannelRequestDTO request);
}
