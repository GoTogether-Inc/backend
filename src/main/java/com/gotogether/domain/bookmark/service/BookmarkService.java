package com.gotogether.domain.bookmark.service;

import com.gotogether.domain.bookmark.dto.response.BookmarkListResponseDTO;
import com.gotogether.domain.bookmark.entity.Bookmark;

import java.util.List;

public interface BookmarkService {
    Bookmark createBookmark(Long eventId, Long userId);

    List<BookmarkListResponseDTO> getUserBookmarks(Long userId);

    void deleteBookmark(Long bookmarkId);
}