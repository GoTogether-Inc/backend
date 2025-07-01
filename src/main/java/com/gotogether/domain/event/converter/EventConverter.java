package com.gotogether.domain.event.converter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
import com.gotogether.global.util.DateUtil;

public class EventConverter {

	public static Event of(EventRequestDTO request, HostChannel hostChannel) {
		return Event.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.startDate(request.getStartDate())
			.endDate(request.getEndDate())
			.bannerImageUrl(request.getBannerImageUrl())
			.address(request.getAddress())
			.detailAddress(request.getDetailAddress())
			.locationLat(request.getLocationLat())
			.locationLng(request.getLocationLng())
			.onlineType(request.getOnlineType())
			.category(request.getCategory())
			.organizerEmail(request.getOrganizerEmail())
			.organizerPhoneNumber(request.getOrganizerPhoneNumber())
			.hostChannel(hostChannel)
			.build();
	}

	public static EventDetailResponseDTO toEventDetailResponseDTO(Event event, Long participantCount, Long bookmarkId) {
		List<ReferenceLinkDTO> links = event.getReferenceLinks().stream()
			.map(link -> ReferenceLinkDTO.builder()
				.title(link.getName())
				.url(link.getToGoUrl())
				.build())
			.collect(Collectors.toList());

		return EventDetailResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.participantCount(participantCount)
			.startDate(String.valueOf(event.getStartDate()))
			.endDate(String.valueOf(event.getEndDate()))
			.address(event.getAddress())
			.detailAddress(event.getDetailAddress())
			.locationLat(event.getLocationLat())
			.locationLng(event.getLocationLng())
			.description(event.getDescription())
			.hostChannelName(event.getHostChannel().getName())
			.hostChannelDescription(event.getHostChannel().getDescription())
			.organizerEmail(event.getOrganizerEmail())
			.organizerPhoneNumber(event.getOrganizerPhoneNumber())
			.referenceLinks(links)
			.category(String.valueOf(event.getCategory()))
			.onlineType(String.valueOf(event.getOnlineType()))
			.status(String.valueOf(event.getStatus()))
			.bookmarkId(bookmarkId)
			.isBookmarked(bookmarkId != null)
			.build();
	}

	public static EventListResponseDTO toEventListResponseDTO(Event event) {
		return EventListResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.hostChannelName(event.getHostChannel().getName())
			.startDate(String.valueOf(event.getStartDate()))
			.endDate(String.valueOf(event.getEndDate()))
			.address(event.getAddress())
			.onlineType(String.valueOf(event.getOnlineType()))
			.hashtags(event.getHashtags().stream()
				.map(Hashtag::getName)
				.distinct()
				.collect(Collectors.toList()))

			.remainDays(DateUtil.getDdayStatus(
				LocalDate.from(event.getStartDate()),
				LocalDate.from(event.getEndDate())))
			.status(String.valueOf(event.getStatus()))
			.build();
	}
}