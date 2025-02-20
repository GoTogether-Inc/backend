package com.gotogether.domain.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.repository.BookmarkRepository;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EventFacade eventFacade;

    @Override
    @Transactional
    public Bookmark createBookmark(Long eventId, Long userId) {
        Event event = eventFacade.getEventById(eventId);
        User user = eventFacade.getUserById(userId);

        if (bookmarkRepository.existsByEventAndUser(event, user)) {
            throw new GeneralException(ErrorStatus._BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = new Bookmark(user, event);
        bookmarkRepository.save(bookmark);

        return bookmark;
    }
}