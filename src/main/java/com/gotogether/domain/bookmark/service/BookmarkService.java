package com.gotogether.domain.bookmark.service;

import com.gotogether.domain.bookmark.entity.Bookmark;

public interface BookmarkService {
    Bookmark createBookmark(Long eventId, Long userId);
}