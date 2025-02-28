package com.gotogether.domain.bookmark.service;

import com.gotogether.domain.bookmark.dto.response.BookmarkListResponseDto;
import com.gotogether.domain.bookmark.entity.Bookmark;

import java.util.List;

public interface BookmarkService {
    Bookmark createBookmark(Long eventId, Long userId);

    List<BookmarkListResponseDto> getUserBookmarks(Long userId);

    void deleteBookmark(Long bookmarkId);
}