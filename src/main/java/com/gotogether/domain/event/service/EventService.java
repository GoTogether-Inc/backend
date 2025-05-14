package com.gotogether.domain.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;

public interface EventService {
	Event createEvent(EventRequestDTO request);

	EventDetailResponseDTO getDetailEvent(Long userId, Long eventId);

	Event updateEvent(Long eventId, EventRequestDTO request);

	void deleteEvent(Long eventId);

	Page<EventListResponseDTO> getEventsByTag(String tag, Pageable pageable);

	Page<EventListResponseDTO> searchEvents(String keyword, Pageable pageable);

	Page<EventListResponseDTO> getEventsByCategory(Category category, Pageable pageable);

	void updateEventStatusToCompleted(Long eventId);
}
