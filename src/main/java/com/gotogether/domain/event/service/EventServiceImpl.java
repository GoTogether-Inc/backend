package com.gotogether.domain.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.converter.EventConverter;
import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.EventStatus;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hashtag.service.HashtagService;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.service.ReferenceLinkService;
import com.gotogether.global.scheduler.EventScheduler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final HashtagService hashtagService;
	private final ReferenceLinkService referenceLinkService;
	private final EventFacade eventFacade;
	private final EventScheduler eventScheduler;

	@Override
	@Transactional
	public Event createEvent(EventRequestDTO request) {
		HostChannel hostChannel = eventFacade.getHostChannelById(request.getHostChannelId());
		Event event = EventConverter.of(request, hostChannel);

		eventRepository.save(event);

		if (!request.getReferenceLinks().isEmpty()) {
			referenceLinkService.createReferenceLinks(event, request.getReferenceLinks());
		}

		if (!request.getHashtags().isEmpty()) {
			hashtagService.createHashtags(event, request.getHashtags());
		}

		eventScheduler.scheduleUpdateEventStatus(event.getId(), event.getEndDate());
		return event;
	}

	@Override
	@Transactional(readOnly = true)
	public EventDetailResponseDTO getDetailEvent(Long eventId) {
		Event event = eventFacade.getEventById(eventId);
		HostChannel hostChannel = eventFacade.getHostChannelById(event.getHostChannel().getId());

		return EventConverter.toEventDetailResponseDTO(event, hostChannel);
	}

	@Override
	@Transactional
	public Event updateEvent(Long eventId, EventRequestDTO request) {
		Event event = eventFacade.getEventById(eventId);
		event.update(request);

		eventRepository.save(event);

		if (!request.getReferenceLinks().isEmpty()) {
			referenceLinkService.deleteAll(event.getReferenceLinks());
			referenceLinkService.createReferenceLinks(event, request.getReferenceLinks());
		}

		if (!request.getHashtags().isEmpty()) {
			hashtagService.deleteHashtagsByRequest(event, request.getHashtags());
			hashtagService.createHashtags(event, request.getHashtags());
		}

		return event;
	}

	@Override
	@Transactional
	public void deleteEvent(Long eventId) {
		Event event = eventFacade.getEventById(eventId);
		hashtagService.deleteHashtagsByEvent(event);

		eventScheduler.deleteScheduledEventJob(eventId);
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

	@Override
	@Transactional(readOnly = true)
	public Page<EventListResponseDTO> getEventsByCategory(Category category, Pageable pageable) {
		Page<Event> events = eventRepository.findByCategory(category, pageable);
		return events.map(EventConverter::toEventListResponseDTO);
	}

	@Override
	@Transactional
	public void updateEventStatusToCompleted(Long eventId) {
		Event event = eventFacade.getEventById(eventId);
		event.updateStatus(EventStatus.COMPLETED);
	}
}