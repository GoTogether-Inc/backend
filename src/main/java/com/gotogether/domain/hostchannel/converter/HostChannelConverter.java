package com.gotogether.domain.hostchannel.converter;

import java.util.List;

import com.gotogether.domain.channelorganizer.entity.ChannelOrganizer;
import com.gotogether.domain.event.converter.EventConverter;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hostchannel.dto.request.HostChannelRequestDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelDetailResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelInfoResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelListResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostChannelMemberResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.HostDashboardResponseDTO;
import com.gotogether.domain.hostchannel.dto.response.ParticipantManagementResponseDTO;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.order.entity.OrderStatus;

public class HostChannelConverter {

	public static HostChannel of(HostChannelRequestDTO request) {
		return HostChannel.builder()
			.profileImageUrl(request.getProfileImageUrl())
			.name(request.getHostChannelName().trim())
			.email(request.getHostEmail())
			.description(request.getChannelDescription())
			.build();
	}

	public static HostChannelListResponseDTO toHostChannelListResponseDTO(HostChannel hostChannel) {
		return HostChannelListResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.build();
	}

	public static HostChannelDetailResponseDTO toHostChannelDetailResponseDTO(HostChannel hostChannel) {
		List<EventListResponseDTO> eventListResponseDTOList = hostChannel.getEvents().stream()
			.map(EventConverter::toEventListResponseDTO)
			.toList();

		return HostChannelDetailResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.channelDescription(hostChannel.getDescription())
			.events(eventListResponseDTOList)
			.build();
	}

	public static HostChannelInfoResponseDTO toHostChannelInfoResponseDTO(HostChannel hostChannel,
		List<HostChannelMemberResponseDTO> members) {

		return HostChannelInfoResponseDTO.builder()
			.id(hostChannel.getId())
			.profileImageUrl(hostChannel.getProfileImageUrl())
			.hostChannelName(hostChannel.getName())
			.channelDescription(hostChannel.getDescription())
			.email(hostChannel.getEmail())
			.hostChannelMembers(members)
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
			.orderId(order.getId()) //TODO 주문 고유 번호로 수정
			.ticketId(order.getTicket().getId())
			.participant(order.getUser().getName())
			.email(order.getUser().getEmail())
			.phoneNumber(order.getUser().getPhoneNumber())
			.purchaseDate(String.valueOf(order.getCreatedAt()))
			.ticketName(order.getTicket().getName())
			.ticketType(order.getTicket().getType().name())
			.isCheckedIn(order.getTicketQrCode() != null && order.getTicketQrCode().getStatus().isCheckIn())
			.isApproved(order.getStatus() == OrderStatus.COMPLETED)
			.build();
	}

	public static HostDashboardResponseDTO toHostDashboardResponseDTO(Event event, Long totalTicketCnt,
		Long totalPrice) {

		return HostDashboardResponseDTO.builder()
			.eventName(event.getTitle())
			.isTicket(!event.getTickets().isEmpty())
			.isTicketOption(false) //TODO 옵션 데이터 추가
			.eventStartDate(String.valueOf(event.getStartDate()))
			.eventEndDate(String.valueOf(event.getEndDate()))
			.totalTicketCnt(totalTicketCnt)
			.totalPrice(totalPrice)
			.build();
	}
}
