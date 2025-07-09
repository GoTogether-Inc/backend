package com.gotogether.domain.hostchannel.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.channelorganizer.repository.ChannelOrganizerRepository;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hostchannel.converter.HostChannelConverter;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.entity.HostChannelStatus;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;
import com.gotogether.domain.order.repository.OrderCustomRepository;
import com.gotogether.domain.order.repository.OrderRepository;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.domain.ticket.repository.TicketRepository;
import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;
import com.gotogether.domain.ticketqrcode.service.TicketQrCodeService;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.common.service.S3UploadService;
import com.gotogether.global.util.ExcelGenerator;
import com.gotogether.domain.ticketoptionassignment.entity.TicketOptionAssignment;
import com.gotogether.domain.ticketoptionassignment.repository.TicketOptionAssignmentRepository;
import com.gotogether.domain.ticketoptionanswer.repository.TicketOptionAnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostChannelServiceImpl implements HostChannelService {

	private final HostChannelRepository hostChannelRepository;
	private final ChannelOrganizerRepository channelOrganizerRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderCustomRepository orderCustomRepository;
	private final TicketRepository ticketRepository;
	private final EventRepository eventRepository;
	private final EventFacade eventFacade;
	private final TicketQrCodeService ticketQrCodeService;
	private final S3UploadService s3UploadService;
	private final TicketOptionAssignmentRepository ticketOptionAssignmentRepository;
	private final TicketOptionAnswerRepository ticketOptionAnswerRepository;

	@Override
	@Transactional
	public HostChannel createHostChannel(Long userId, HostChannelRequestDTO request) {
		User user = getUser(userId);

		Optional<HostChannel> existingHostChannel = hostChannelRepository.findByName(
			request.getHostChannelName());

		if (existingHostChannel.isPresent()) {
			HostChannel hostChannel = existingHostChannel.get();

			if (channelOrganizerRepository.existsByUserAndHostChannel(user, hostChannel)) {
				hostChannel.updateStatus(HostChannelStatus.ACTIVE);
				return hostChannel;
			}
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_EXISTS);
		}

		HostChannel newHostChannel = HostChannelConverter.of(request);
		hostChannelRepository.save(newHostChannel);

		ChannelOrganizer channelOrganizer = createChannelOrganizer(user, newHostChannel);
		channelOrganizerRepository.save(channelOrganizer);

		updateProfileImageToFinal(newHostChannel, request.getProfileImageUrl());

		return newHostChannel;
	}

	@Override
	@Transactional(readOnly = true)
	public List<HostChannelListResponseDTO> getHostChannels(Long userId) {
		User user = getUser(userId);
		List<HostChannel> hostChannels = hostChannelRepository.findActiveHostChannelsByUser(user,
			HostChannelStatus.INACTIVE);

		return hostChannels.stream()
			.map(HostChannelConverter::toHostChannelListResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public HostChannelDetailResponseDTO getDetailHostChannel(Long hostChannelId) {
		HostChannel hostChannel = hostChannelRepository.findById(hostChannelId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));

		List<Event> events = eventRepository.findAllByHostChannelId(hostChannelId);

		return HostChannelConverter.toHostChannelDetailResponseDTO(hostChannel, events);
	}

	@Override
	@Transactional(readOnly = true)
	public HostChannelInfoResponseDTO getHostChannelInfo(Long hostChannelId) {
		HostChannel hostChannel = eventFacade.getHostChannelById(hostChannelId);

		List<HostChannelMemberResponseDTO> members = getMembers(hostChannelId);

		return HostChannelConverter.toHostChannelInfoResponseDTO(hostChannel, members);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<HostChannelListResponseDTO> searchHostChannels(String keyword, Pageable pageable) {
		Page<HostChannel> hostChannels = hostChannelRepository.findHostChannelByFilter(keyword, pageable);
		return hostChannels.map(HostChannelConverter::toHostChannelListResponseDTO);
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

		s3UploadService.deleteFile(hostChannel.getProfileImageUrl());

		hostChannel.update(request);

		updateProfileImageToFinal(hostChannel, request.getProfileImageUrl());

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

		List<ChannelOrganizer> organizers = channelOrganizerRepository.findChannelOrganizerWithUserByHostChannel(
			hostChannel);

		return organizers.stream()
			.map(HostChannelConverter::toHostChannelMemberResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ParticipantManagementResponseDTO> getParticipantManagement(Long eventId, String tag,
		Pageable pageable) {

		List<Ticket> tickets = ticketRepository.findByEventId(eventId);

		List<Long> ticketIds = tickets.stream()
			.map(Ticket::getId)
			.collect(Collectors.toList());

		Page<Order> orders = orderCustomRepository.findByTicketIdsAndStatus(ticketIds, tag, pageable);

		return orders.stream()
			.map(HostChannelConverter::toParticipantManagementResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public HostDashboardResponseDTO getHostDashboard(Long eventId) {
		Event event = eventFacade.getEventById(eventId);

		List<Order> orders = orderRepository.findCompletedOrdersByEventId(eventId, OrderStatus.COMPLETED);

		long totalTicketCnt = orders.size();
		long totalPrice = orders.stream()
			.mapToLong(order -> order.getTicket().getPrice())
			.sum();

		return HostChannelConverter.toHostDashboardResponseDTO(event, totalTicketCnt, totalPrice);
	}

	@Override
	@Transactional
	public void approveOrderStatus(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._ORDER_NOT_FOUND));

		validateOrderStatus(order.getStatus());

		if (order.getTicket().getEvent().getOnlineType() == OnlineType.OFFLINE) {
			TicketQrCode ticketQrCode = ticketQrCodeService.createQrCode(order);
			order.updateTicketQrCode(ticketQrCode);

			orderRepository.save(order);
		}
		order.approveOrder();
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] generateParticipantManagementExcel(Long eventId) {
		List<Ticket> tickets = ticketRepository.findByEventId(eventId);
		List<Order> orders = orderRepository.findCompletedOrdersByEventId(eventId, OrderStatus.COMPLETED);
		
		Set<String> optionNames = extractOptionNames(tickets);
		
		try {
			return ExcelGenerator.generateParticipantExcel(orders, optionNames, 
				orderId -> ticketOptionAnswerRepository.findByOrderId(orderId));
		} catch (RuntimeException e) {
			throw new GeneralException(ErrorStatus._EXCEL_GENERATION_FAILED);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public String generateParticipantManagementExcelFileName(Long eventId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._EVENT_NOT_FOUND));
		
		String eventName = event.getTitle()
			.replaceAll("[\\/:*?\"<>|]", "") 
			.replaceAll("\\s+", "_");
		
		String currentDate = java.time.LocalDate.now().toString();
		
		return String.format("%s_구매참가자목록_%s.xlsx", eventName, currentDate);
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
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_DELETE_FAILED_MEMBERS_EXIST);
		}
	}

	private void validateHostChannelExistMember(User user, HostChannel hostChannel) {
		if (channelOrganizerRepository.existsByUserAndHostChannel(user, hostChannel)) {
			throw new GeneralException(ErrorStatus._HOST_CHANNEL_MEMBER_ALREADY_EXISTS);
		}
	}

	private void validateOrderStatus(OrderStatus status) {
		if (status == OrderStatus.COMPLETED) {
			throw new GeneralException(ErrorStatus._ORDER_ALREADY_COMPLETED);
		}
		if (status == OrderStatus.CANCELED) {
			throw new GeneralException(ErrorStatus._ORDER_ALREADY_CANCELED);
		}
	}

	private void updateProfileImageToFinal(HostChannel hostChannel, String imageUrl) {
		String finalUrl = s3UploadService.moveTempImageToFinal(imageUrl);
		hostChannel.updateProfileImageUrl(finalUrl);
	}

	private Set<String> extractOptionNames(List<Ticket> tickets) {
		Set<String> optionNames = new LinkedHashSet<>();
		
		for (Ticket ticket : tickets) {
			List<TicketOptionAssignment> assignments = ticketOptionAssignmentRepository.findAllByTicket(ticket);
			
			for (TicketOptionAssignment assignment : assignments) {
				if (assignment != null && assignment.getTicketOption() != null) {
					String optionName = assignment.getTicketOption().getName();
					if (optionName != null && !optionName.trim().isEmpty()) {
						optionNames.add(optionName);
					}
				}
			}
		}
		
		return optionNames;
	}
}
