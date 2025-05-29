package com.gotogether.domain.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.repository.BookmarkRepository;
import com.gotogether.domain.event.converter.EventConverter;
import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.entity.EventStatus;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.event.repository.EventCustomRepository;
import com.gotogether.domain.event.repository.EventRepository;
import com.gotogether.domain.hashtag.service.HashtagService;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.service.ReferenceLinkService;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.scheduler.EventScheduler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventFacade eventFacade;
	private final EventRepository eventRepository;
	private final EventCustomRepository eventCustomRepository;
	private final BookmarkRepository bookmarkRepository;
	private final HashtagService hashtagService;
	private final ReferenceLinkService referenceLinkService;
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
	public EventDetailResponseDTO getDetailEvent(Long userId, Long eventId) {
		Event event = eventCustomRepository.findEventWithDetails(eventId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._EVENT_NOT_FOUND));

		Long bookmarkId = null;
		if (userId != null) {
			User user = eventFacade.getUserById(userId);

			bookmarkId = bookmarkRepository.findByEventAndUser(event, user)
				.map(Bookmark::getId)
				.orElse(null);
		}

		return EventConverter.toEventDetailResponseDTO(event, bookmarkId);
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
		event.updateStatus(EventStatus.DELETED);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EventListResponseDTO> getEventsByTag(String tag, Pageable pageable) {
		Page<Event> events = eventCustomRepository.findEventsByTag(tag, pageable);
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