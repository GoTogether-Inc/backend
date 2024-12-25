package com.gotogether.domain.event.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.entity.ReferenceLink;

public class EventConverter {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public static Event of(EventRequestDTO request, HostChannel hostChannel) {
		return Event.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.startDate(request.getStartDate().atTime(LocalTime.parse(request.getStartTime())))
			.endDate(request.getEndDate().atTime(LocalTime.parse(request.getEndTime())))
			.bannerImageUrl(request.getBannerImageUrl())
			.location(request.getLocation())
			.onlineType(request.getOnlineType())
			.category(request.getCategory())
			.hostEmail(request.getHostEmail())
			.hostPhoneNumber(request.getHostPhoneNumber())
			.hostChannel(hostChannel)
			.build();
	}

	public static EventDetailResponseDTO toEventDetailResponseDTO(Event event, HostChannel hostChannel) {
		Map<String, String> links = event.getReferenceLinks().stream()
			.filter(link -> !link.isDeleted())
			.collect(Collectors.toMap(
				ReferenceLink::getName,
				ReferenceLink::getToGoUrl
			));

		long ticketCount = event.getTickets().stream()
			.filter(ticket -> !ticket.isDeleted())
			.count();

		return EventDetailResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.participantCount((int)ticketCount)
			.startDate(event.getStartDate().format(DATE_FORMATTER))
			.endDate(event.getEndDate().format(DATE_FORMATTER))
			.startTime(event.getStartDate().format(TIME_FORMATTER))
			.endTime(event.getEndDate().format(TIME_FORMATTER))
			.location(event.getLocation())
			.description(event.getDescription())
			.hostChannelName(hostChannel.getName())
			.hostChannelDescription(hostChannel.getDescription())
			.hostEmail(event.getHostEmail())
			.hostPhoneNumber(event.getHostPhoneNumber())
			.referenceLinks(links)
			.build();
	}

	public static EventListResponseDTO toEventListResponseDTO(Event event) {
		return EventListResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.hostChannelName(event.getHostChannel().getName())
			.startDate(event.getStartDate().toLocalDate().format(DATE_FORMATTER))
			.location(event.getLocation())
			.hashtags(event.getHashtags().stream()
				.map(Hashtag::getName)
				.collect(Collectors.toList()))
			.build();
	}
}