package com.gotogether.domain.event.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.dto.request.CreateEventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.repository.HashtagRepository;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.referencelink.entity.ReferenceLink;
import com.gotogether.domain.referencelink.repository.ReferenceLinkRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final ReferenceLinkRepository referenceLinkRepository;
	private final HashtagRepository hashtagRepository;
	private final HostChannelRepository hostChannelRepository;

	@Override
	public void createEvent(CreateEventRequestDTO createEventRequestDTO, Long userId) {

		Event event = Event.builder()
			.hostChannel(getHostChannel(createEventRequestDTO.getHostChannelId()))
			.title(createEventRequestDTO.getTitle())
			.startDate(createEventRequestDTO.getStartDateTime())
			.endDate(createEventRequestDTO.getEndDateTime())
			.bannerImageUrl(createEventRequestDTO.getBannerImageUrl())
			.description(createEventRequestDTO.getDescription())
			.onlineType(createEventRequestDTO.getOnlineType())
			.location(createEventRequestDTO.getLocation())
			.category(createEventRequestDTO.getCategory())
			.hostEmail(createEventRequestDTO.getHostEmail())
			.hostPhoneNumber(createEventRequestDTO.getHostPhoneNumber())
			.build();

		eventRepository.save(event);

		if (createEventRequestDTO.getReferenceLinks() != null) {
			saveReferenceLinks(event, createEventRequestDTO.getReferenceLinks());
		}

		if (createEventRequestDTO.getHashtags() != null) {
			saveHashtags(event, createEventRequestDTO.getHashtags());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public EventDetailResponseDTO getDetailEvent(Long eventId) {

		Event event = getEvent(eventId);

		HostChannel hostChannel = getHostChannel(event.getHostChannel().getId());

		return EventDetailResponseDTO.fromEntity(event, hostChannel);

	}

	@Override
	public void deleteEvent(Long eventId) {
		Event event = getEvent(eventId);
		eventRepository.delete(event);
	}

	private HostChannel getHostChannel(Long hostChannelId) {
		return hostChannelRepository.findById(hostChannelId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));
	}

	private void saveHashtags(Event event, List<String> hashtags) {
		List<Hashtag> hashtagList = hashtags.stream()
			.map(hashtag -> Hashtag.builder().event(event).name(hashtag).build())
			.collect(Collectors.toList());

		hashtagRepository.saveAll(hashtagList);
	}

	private void saveReferenceLinks(Event event, Map<String, String> referenceLinks) {
		List<ReferenceLink> referenceLinkList = referenceLinks.entrySet()
			.stream()
			.map(entry -> ReferenceLink.builder().event(event).name(entry.getKey()).toGoUrl(entry.getValue()).build())
			.collect(Collectors.toList());

		referenceLinkRepository.saveAll(referenceLinkList);
	}

	private Event getEvent(Long eventId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._EVENT_NOT_FOUND));

		if (event.isDeleted()) {
			throw new GeneralException(ErrorStatus._EVENT_ALREADY_DELETED);
		}

		return event;
	}
}