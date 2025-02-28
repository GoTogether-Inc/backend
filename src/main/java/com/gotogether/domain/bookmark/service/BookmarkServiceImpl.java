package com.gotogether.domain.bookmark.service;

import com.gotogether.domain.bookmark.dto.response.BookmarkListResponseDto;
import com.gotogether.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.repository.BookmarkRepository;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.event.facade.EventFacade;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.domain.bookmark.converter.BookmarkConverter;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EventFacade eventFacade;
    private final UserRepository userRepository;


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

        return bookmark;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookmarkListResponseDto> getUserBookmarks(Long userId) {
        User user = getUser(userId);

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);

        return bookmarks.stream()
                .map(BookmarkConverter::toBookmarkListResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
    }
}