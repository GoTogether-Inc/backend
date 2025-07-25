package com.gotogether.domain.bookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.bookmark.converter.BookmarkConverter;
import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.repository.BookmarkRepository;
import com.gotogether.domain.event.converter.EventConverter;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.service.MetricService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final EventFacade eventFacade;
	private final MetricService metricService;

	@Override
	@Transactional
	public Bookmark createBookmark(Long eventId, Long userId) {
		Event event = eventFacade.getEventById(eventId);
		User user = eventFacade.getUserById(userId);

		if (bookmarkRepository.existsByEventAndUser(event, user)) {
			throw new GeneralException(ErrorStatus._BOOKMARK_ALREADY_EXISTS);
		}

		Bookmark bookmark = BookmarkConverter.of(user, event);
		bookmarkRepository.save(bookmark);

		metricService.recordBookmarkCreation(eventId);

		return bookmark;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventListResponseDTO> getUserBookmarks(Long userId) {
		List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);

		return bookmarks.stream()
			.map(bookmark -> EventConverter.toEventListResponseDTO(bookmark.getEvent()))
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteBookmark(Long bookmarkId) {
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._BOOKMARK_NOT_FOUND));

		bookmarkRepository.delete(bookmark);
	}
}