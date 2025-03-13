package com.gotogether.domain.hostchannel.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.channelorganizer.repository.ChannelOrganizerRepository;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.hostchannel.converter.HostChannelConverter;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.entity.HostChannelStatus;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;
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
	private final OrderRepository orderRepository;
	private final TicketRepository ticketRepository;
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

	@Override
	@Transactional(readOnly = true)
	public List<ParticipantManagementResponseDTO> getParticipantManagement(Long eventId,
		String tags, Pageable pageable) {

		List<Ticket> tickets = ticketRepository.findByEventId(eventId);

		List<Long> ticketIds = tickets.stream()
			.map(Ticket::getId)
			.collect(Collectors.toList());

		Page<Order> orders;

		if (tags.equals("approved")) {
			orders = orderRepository.findByTicketIdInAndStatus(ticketIds, OrderStatus.COMPLETED, pageable);
		} else if (tags.equals("pending")) {
			orders = orderRepository.findByTicketIdInAndStatus(ticketIds, OrderStatus.PENDING, pageable);
		} else {
			orders = orderRepository.findByTicketIdInAndStatusNot(ticketIds, OrderStatus.CANCELED, pageable);
		}

		return orders.stream()
			.map(HostChannelConverter::toParticipantManagementResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public HostDashboardResponseDTO getHostDashboard(Long eventId) {
		Event event = eventFacade.getEventById(eventId);

		List<Ticket> tickets = ticketRepository.findByEventId(eventId);

		Long totalTicketCnt = 0L;
		Long totalPrice = 0L;

		for (Ticket ticket : tickets) {
			List<Order> orders = orderRepository.findByTicketAndStatus(ticket, OrderStatus.COMPLETED);

			totalTicketCnt += orders.size();
			totalPrice += orders.size() * ticket.getPrice();
		}

		return HostChannelConverter.toHostDashboardResponseDTO(event, totalTicketCnt, totalPrice);
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
