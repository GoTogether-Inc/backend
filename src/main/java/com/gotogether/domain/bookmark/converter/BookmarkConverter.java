package com.gotogether.domain.bookmark.converter;

import java.util.stream.Collectors;

import com.gotogether.domain.bookmark.dto.response.BookmarkListResponseDTO;
import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.user.entity.User;

public class BookmarkConverter {

	public static Bookmark of(User user, Event event) {
		return Bookmark.builder()
			.user(user)
			.event(event)
			.build();
	}

	public static BookmarkListResponseDTO toBookmarkListResponseDTO(Bookmark bookmark) {
		Event event = bookmark.getEvent();

		return BookmarkListResponseDTO.builder()
			.eventId(event.getId())
			.eventTitle(event.getTitle())
			.eventBanner(event.getBannerImageUrl())
			.hostChannelName(event.getHostChannel().getName())
			.eventStartDate(event.getStartDate().toString())
			.eventAddress(event.getAddress())
			.eventHashtags(event.getHashtags().stream()
				.map(Hashtag::getName)
				.collect(Collectors.toList()))
			.build();
	}
}