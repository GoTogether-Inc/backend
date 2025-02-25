package com.gotogether.domain.bookmark.converter;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.user.entity.User;

public class BookmarkConverter {

    public static Bookmark of(User user, Event event) {
        return Bookmark.builder()
                .user(user)
                .event(event)
                .build();
    }
}