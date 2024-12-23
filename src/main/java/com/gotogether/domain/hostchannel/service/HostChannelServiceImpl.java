package com.gotogether.domain.hostchannel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.channelorganizer.repository.ChannelOrganizerRepository;
import com.gotogether.domain.hostchannel.converter.HostChannelConverter;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
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

	@Override
	@Transactional(readOnly = true)
	public Page<HostChannelListResponseDTO> getHostChannels(Long userId, Pageable pageable) {
		User user = getUser(userId);
		Page<HostChannel> hostChannels = hostChannelRepository.findByUser(user, pageable);

		return hostChannels.map(HostChannelConverter::toHostChannelListResponseDTO);
	}

	@Override
	@Transactional
	public void deleteHostChannel(Long hostChannelId) {
		HostChannel hostChannel = getHostChannel(hostChannelId);
		validateHostChannelDelete(hostChannel);

		channelOrganizerRepository.deleteByHostChannel(hostChannel);
		hostChannelRepository.delete(hostChannel);
	}

	@Override
	@Transactional
	public HostChannel updateHostChannel(Long hostChannelId, HostChannelRequestDTO request) {
		HostChannel hostChannel = getHostChannel(hostChannelId);
		hostChannel.update(request);

		return hostChannelRepository.save(hostChannel);
	}

	@Override
	@Transactional
	public void addMember(Long hostChannelId, String email) {
		HostChannel hostChannel = getHostChannel(hostChannelId);
		User user = getUserByEmail(email);

		validateHostChannelExistMember(user, hostChannel);

		createChannelOrganizer(user, hostChannel);
		channelOrganizerRepository.save(createChannelOrganizer(user, hostChannel));
	}

	private User getUser(Long userId) {
		return userRepository.findByIdAndIsDeletedFalse(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private HostChannel getHostChannel(Long hostChannelId) {
		return hostChannelRepository.findByIdAndIsDeletedFalse(hostChannelId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmailAndIsDeletedFalse(email)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private ChannelOrganizer createChannelOrganizer(User user, HostChannel hostChannel) {
		return ChannelOrganizer.builder()
			.user(user)
			.hostChannel(hostChannel)
			.build();
	}

	private void validateHostChannelDelete(HostChannel hostChannel) {
		long organizerCount = channelOrganizerRepository.countByHostChannel(hostChannel);

		if (organizerCount > 1) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_DELETE_FAILED_MEMBERS_EXIST);
		}
	}

	private void validateHostChannelExistMember(User user, HostChannel hostChannel) {
		if (channelOrganizerRepository.existsByUserAndHostChannel(user, hostChannel)) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_MEMBER_ALREADY_EXISTS);
		}
	}

}
