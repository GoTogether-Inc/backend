package com.gotogether.domain.bookmark.converter;

import com.gotogether.domain.bookmark.dto.response.BookmarkListResponseDto;
import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.user.entity.User;

import java.util.stream.Collectors;

public class BookmarkConverter {

    public static Bookmark of(User user, Event event) {
        return Bookmark.builder()
                .user(user)
                .event(event)
                .build();
    }

    public static BookmarkListResponseDto toBookmarkListResponseDTO(Bookmark bookmark) {
        Event event = bookmark.getEvent();

        return BookmarkListResponseDto.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventBanner(event.getBannerImageUrl())
                .hostChannelName(event.getHostChannel().getName())
                .eventStartDate(event.getStartDate().toString())
                .eventLocation(event.getLocation())
                .eventHashtags(event.getHashtags().stream()
                        .map(Hashtag::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}