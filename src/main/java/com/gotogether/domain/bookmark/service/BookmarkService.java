package com.gotogether.domain.bookmark.service;

import java.util.List;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;

public interface BookmarkService {
	Bookmark createBookmark(Long eventId, Long userId);

	List<EventListResponseDTO> getUserBookmarks(Long userId);

	void deleteBookmark(Long bookmarkId);
}