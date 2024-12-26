package com.gotogether.domain.event.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.converter.EventConverter;
import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.eventhashtag.entity.EventHashtag;
import com.gotogether.domain.eventhashtag.repository.EventHashtagRepository;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.repository.HashtagRepository;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.hostchannel.repository.HostChannelRepository;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;
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
	private final EventHashtagRepository eventHashtagRepository;

	@Override
	@Transactional
	public Event createEvent(EventRequestDTO request) {
		HostChannel hostChannel = getHostChannel(request.getHostChannelId());
		Event event = EventConverter.of(request, hostChannel);

		eventRepository.save(event);

		if (request.getReferenceLinks() != null) {
			saveReferenceLinks(event, request.getReferenceLinks());
		}

		if (request.getHashtags() != null) {
			saveHashtags(event, request.getHashtags());
		}

		return event;
	}

	@Override
	@Transactional(readOnly = true)
	public EventDetailResponseDTO getDetailEvent(Long eventId) {
		Event event = getEvent(eventId);
		HostChannel hostChannel = getHostChannel(event.getHostChannel().getId());

		return EventConverter.toEventDetailResponseDTO(event, hostChannel);
	}

	@Override
	@Transactional
	public Event updateEvent(Long eventId, EventRequestDTO request) {
		Event event = getEvent(eventId);
		event.update(request);

		eventRepository.save(event);

		if (request.getReferenceLinks() != null) {
			referenceLinkRepository.deleteAll(event.getReferenceLinks());
			saveReferenceLinks(event, request.getReferenceLinks());
		}

		if (request.getHashtags() != null) {
			deleteUnusedHashtags(event, request.getHashtags());
			saveHashtags(event, request.getHashtags());
		}

		return event;
	}

	@Override
	@Transactional
	public void deleteEvent(Long eventId) {
		Event event = getEvent(eventId);

		List<Hashtag> existingHashtags = eventHashtagRepository.findHashtagsByEvent(event);

		eventHashtagRepository.deleteByEvent(event);

		for (Hashtag hashtag : existingHashtags) {
			if (eventHashtagRepository.countByHashtag(hashtag) == 0) {
				hashtagRepository.delete(hashtag);
			}
		}

		eventRepository.delete(event);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EventListResponseDTO> getEventsByTag(String tags, Pageable pageable) {
		Page<Event> events;

		if (tags.equals("deadline")) {
			events = eventRepository.findDeadlineEvents(pageable);
		} else if (tags.equals("popular")) {
			events = eventRepository.findPopularEvents(pageable);
		} else {
			events = eventRepository.findCurrentEvents(pageable);
		}
		return events.map(EventConverter::toEventListResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EventListResponseDTO> searchEvents(String keyword, Pageable pageable) {
		Page<Event> events = eventRepository.findEventsByFilter(keyword, pageable);
		return events.map(EventConverter::toEventListResponseDTO);
	}

	private Event getEvent(Long eventId) {
		return eventRepository.findByIdAndIsDeletedFalse(eventId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._EVENT_NOT_FOUND));
	}

	private HostChannel getHostChannel(Long hostChannelId) {
		return hostChannelRepository.findByIdAndIsDeletedFalse(hostChannelId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HOST_CHANNEL_NOT_FOUND));
	}

	private void saveHashtags(Event event, List<String> hashtags) {
		List<EventHashtag> eventHashtags = hashtags.stream().map(hashtag -> {

			String normalizeHashtag = normalizeHashtag(hashtag);

			Hashtag existingHashtag = hashtagRepository.findByName(normalizeHashtag)
				.orElseGet(() ->
					hashtagRepository.save(Hashtag.builder()
						.name(normalizeHashtag)
						.build()));

			return EventHashtag.builder()
				.event(event)
				.hashtag(existingHashtag)
				.build();

		}).collect(Collectors.toList());

		eventHashtagRepository.saveAll(eventHashtags);
	}

	private void saveReferenceLinks(Event event, List<ReferenceLinkDTO> referenceLinks) {
		List<ReferenceLink> referenceLinkList = referenceLinks.stream()
			.map(link -> ReferenceLink.builder()
				.event(event)
				.name(link.getTitle())
				.toGoUrl(link.getUrl())
				.build())
			.collect(Collectors.toList());

		referenceLinkRepository.saveAll(referenceLinkList);
	}

	private void deleteUnusedHashtags(Event event, List<String> hashtags) {
		List<Hashtag> existingHashtags = eventHashtagRepository.findHashtagsByEvent(event);

		List<Hashtag> unUsedHashtags = existingHashtags.stream()
			.filter(existingHashtag -> hashtags.stream()
				.noneMatch(hashtag -> normalizeHashtag(hashtag).equals(existingHashtag.getName())))
			.toList();

		for (Hashtag hashtag : unUsedHashtags) {
			if (eventHashtagRepository.countByHashtag(hashtag) == 1) {
				hashtagRepository.delete(hashtag);
			}
		}

		eventHashtagRepository.deleteByEvent(event);
	}

	private String normalizeHashtag(String hashtag) {
		if (hashtag == null) {
			return null;
		}

		return hashtag.replaceAll("\\s+", "").toLowerCase();
	}
}
