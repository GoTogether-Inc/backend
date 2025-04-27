package com.gotogether.domain.event.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.event.dto.response.EventDetailResponseDTO;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.service.EventService;
import com.gotogether.global.apipayload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

	private final EventService eventService;

	@PostMapping
	public ApiResponse<?> createEvent(
		@RequestBody @Valid EventRequestDTO request) {
		Event event = eventService.createEvent(request);
		return ApiResponse.onSuccessCreated("eventId: " + event.getId());
	}

	@GetMapping("/{eventId}")
	public ApiResponse<EventDetailResponseDTO> getDetailEvent(
		@PathVariable Long eventId) {
		return ApiResponse.onSuccess(eventService.getDetailEvent(eventId));
	}

	@PutMapping("/{eventId}")
	public ApiResponse<?> updateEvent(
		@PathVariable Long eventId,
		@RequestBody @Valid EventRequestDTO request) {
		Event event = eventService.updateEvent(eventId, request);
		return ApiResponse.onSuccess("eventId: " + event.getId());
	}

	@DeleteMapping("/{eventId}")
	public ApiResponse<?> deleteEvent(@PathVariable Long eventId) {
		eventService.deleteEvent(eventId);
		return ApiResponse.onSuccess("이벤트 삭제 성공");
	}

	@GetMapping
	public ApiResponse<List<EventListResponseDTO>> getEvents(
		@RequestParam(name = "tags", defaultValue = "current") String tags,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<EventListResponseDTO> events = eventService.getEventsByTag(tags, pageable);
		return ApiResponse.onSuccess(events.getContent());
	}

	@GetMapping("/search")
	public ApiResponse<List<EventListResponseDTO>> getEventsSearch(
		@RequestParam(required = false) String keyword,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<EventListResponseDTO> events = eventService.searchEvents(keyword, pageable);
		return ApiResponse.onSuccess(events.getContent());
	}

	@GetMapping("/categories")
	public ApiResponse<List<EventListResponseDTO>> getEventsByCategory(
		@RequestParam(name = "category") Category category,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<EventListResponseDTO> events = eventService.getEventsByCategory(category, pageable);
		return ApiResponse.onSuccess(events.getContent());
	}
}
