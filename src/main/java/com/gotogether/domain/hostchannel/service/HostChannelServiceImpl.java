package com.gotogether.domain.hostchannel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.channelorganizer.repository.ChannelOrganizerRepository;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hostchannel.converter.HostChannelConverter;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.entity.HostChannelStatus;
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
	private final EventRepository eventRepository;
	private final EventFacade eventFacade;

	@Override
	@Transactional
	public HostChannel createHostChannel(Long userId, HostChannelRequestDTO request) {
		User user = getUser(userId);

		Optional<HostChannel> existingHostChannel = hostChannelRepository.findByNameAndUser(
			request.getHostChannelName(), user);

		if (existingHostChannel.isPresent()) {

			HostChannel hostChannel = existingHostChannel.get();
			hostChannel.updateStatus(HostChannelStatus.ACTIVE);
			return hostChannel;
		}

		if (hostChannelRepository.findByName(request.getHostChannelName().trim()).isPresent()) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_EXISTS);
		}

		HostChannel newHostChannel = HostChannelConverter.toEntity(request);
		hostChannelRepository.save(newHostChannel);

		ChannelOrganizer channelOrganizer = createChannelOrganizer(user, newHostChannel);
		channelOrganizerRepository.save(channelOrganizer);

		return newHostChannel;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<HostChannelListResponseDTO> getHostChannels(Long userId, Pageable pageable) {
		User user = getUser(userId);
		Page<HostChannel> hostChannels = hostChannelRepository.findByUser(user, pageable);

		return hostChannels.map(HostChannelConverter::toHostChannelListResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public HostChannelDetailResponseDTO getDetailHostChannel(Long hostChannelId) {
		HostChannel hostChannel = eventFacade.getHostChannelById(hostChannelId);

		return HostChannelConverter.toHostChannelDetailResponseDTO(hostChannel);
	}

	@Override
	@Transactional
	public void deleteHostChannel(Long hostChannelId) {
		HostChannel hostChannel = eventFacade.getHostChannelById(hostChannelId);
		validateHostChannelDelete(hostChannel);

		hostChannel.updateStatus(HostChannelStatus.INACTIVE);
	}

	@Override
	@Transactional
	public HostChannel updateHostChannel(Long hostChannelId, HostChannelRequestDTO request) {
		HostChannel hostChannel = eventFacade.getHostChannelById(hostChannelId);
		hostChannel.update(request);

		return hostChannelRepository.save(hostChannel);
	}

	@Override
	@Transactional
	public void addMember(Long hostChannelId, String email) {
		HostChannel hostChannel = eventFacade.getHostChannelById(hostChannelId);
		User user = getUserByEmail(email);

		validateHostChannelExistMember(user, hostChannel);

		createChannelOrganizer(user, hostChannel);
		channelOrganizerRepository.save(createChannelOrganizer(user, hostChannel));
	}

	@Override
	@Transactional(readOnly = true)
	public List<HostChannelMemberResponseDTO> getMembers(Long hostChannelId) {
		HostChannel hostChannel = eventFacade.getHostChannelById(hostChannelId);

		List<ChannelOrganizer> organizers = channelOrganizerRepository.findByHostChannel(hostChannel);

		return organizers.stream()
			.map(HostChannelConverter::toHostChannelMemberResponseDTO)
			.toList();
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private ChannelOrganizer createChannelOrganizer(User user, HostChannel hostChannel) {
		return ChannelOrganizer.builder()
			.user(user)
			.hostChannel(hostChannel)
			.build();
	}

	private void validateHostChannelDelete(HostChannel hostChannel) {
		long eventCount = eventRepository.countByHostChannel(hostChannel);

		if (eventCount != 0) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_DELETE_FAILED_EVENTS_EXIST);
		}

		long organizerCount = channelOrganizerRepository.countByHostChannel(hostChannel);

		if (organizerCount > 1) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_DELETE_FAILED_EVENTS_EXIST);
		}
	}

	private void validateHostChannelExistMember(User user, HostChannel hostChannel) {
		if (channelOrganizerRepository.existsByUserAndHostChannel(user, hostChannel)) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_MEMBER_ALREADY_EXISTS);
		}
	}

}
