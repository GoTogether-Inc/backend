package com.gotogether.domain.hostchannel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.channelorganizer.repository.ChannelOrganizerRepository;
import com.gotogether.domain.hostchannel.converter.HostChannelConverter;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostChannelServiceImpl implements HostChannelService {

	private final HostChannelRepository hostChannelRepository;
	private final ChannelOrganizerRepository channelOrganizerRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public HostChannel createHostChannel(Long userId, HostChannelRequestDTO request) {
		User user = getUser(userId);

		HostChannel hostChannel = HostChannelConverter.toEntity(request);
		hostChannelRepository.save(hostChannel);

		ChannelOrganizer channelOrganizer = createChannelOrganizer(user, hostChannel);
		channelOrganizerRepository.save(channelOrganizer);

		return hostChannel;
	}

	private User getUser(Long userId) {
		return userRepository.findByIdAndIsDeletedFalse(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private ChannelOrganizer createChannelOrganizer(User user, HostChannel hostChannel) {
		return ChannelOrganizer.builder().user(user).hostChannel(hostChannel).build();
	}
}
