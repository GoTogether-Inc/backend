package com.gotogether.domain.hostchannel.converter;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.entity.Order;

public class HostChannelConverter {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public static HostChannel toEntity(HostChannelRequestDTO request) {
		return HostChannel.builder()
			.profileImageUrl(request.getProfileImageUrl())
			.name(request.getHostChannelName())
			.email(request.getHostEmail())
			.description(request.getChannelDescription())
			.build();
	}

	public static HostChannelListResponseDTO toHostChannelListResponseDTO(
		HostChannel hostChannel) {
		return HostChannelListResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.build();
	}

	public static HostChannelDetailResponseDTO toHostChannelDetailResponseDTO(
		HostChannel hostChannel) {

		List<EventListResponseDTO> eventListResponseDTOList = hostChannel.getEvents().stream()
			.map(event -> EventListResponseDTO.builder()
				.id(event.getId())
				.bannerImageUrl(event.getBannerImageUrl())
				.title(event.getTitle())
				.hostChannelName(hostChannel.getName())
				.startDate(event.getStartDate().toString())
				.location(event.getLocation())
				.hashtags(
					event.getHashtags().stream()
						.map(Hashtag::getName)
						.toList()
				)
				.build()
			)
			.toList();

		return HostChannelDetailResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.channelDescription(hostChannel.getDescription())
			.events(eventListResponseDTOList)
			.build();
	}

	public static HostChannelMemberResponseDTO toHostChannelMemberResponseDTO(ChannelOrganizer channelOrganizer) {
		return HostChannelMemberResponseDTO.builder()
			.id(channelOrganizer.getUser().getId())
			.memberName(channelOrganizer.getUser().getName())
			.build();
	}

	public static ParticipantManagementResponseDTO toParticipantManagementResponseDTO(Order order) {
		return ParticipantManagementResponseDTO.builder()
			.id(order.getId())
			.ticketNumber(order.getTicketQrCode().getId())
			.participant(order.getUser().getName())
			.email(order.getUser().getEmail())
			.phoneNumber(order.getUser().getPhoneNumber())
			.purchaseDate(order.getCreatedAt().format(DATE_FORMATTER))
			.ticketName(order.getTicket().getName())
			.isCheckedIn(String.valueOf(order.getTicketQrCode().getStatus()))
			.isApproved(String.valueOf(order.getStatus()))
			.build();
	}

	public static HostDashboardResponseDTO toHostDashboardResponseDTO(Event event, Long totalTicketCnt,
		Long totalPrice) {
		return HostDashboardResponseDTO.builder()
			.eventName(event.getTitle())
			.isTicket(!event.getTickets().isEmpty())
			.isTicketOption(false) //TODO 옵션 데이터 추가
			.eventStartDate(event.getStartDate().format(DATE_FORMATTER))
			.eventStartTime(event.getStartDate().format(TIME_FORMATTER))
			.eventEndDate(event.getEndDate().format(DATE_FORMATTER))
			.eventEndTime(event.getEndDate().format(TIME_FORMATTER))
			.totalTicketCnt(totalTicketCnt)
			.totalPrice(totalPrice)
			.build();
	}
}
