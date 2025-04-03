package com.gotogether.domain.bookmark.converter;

import java.time.LocalDate;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.user.entity.User;
import com.gotogether.global.util.DateFormatterUtil;
import com.gotogether.global.util.DateUtil;

public class BookmarkConverter {

	public static Bookmark of(User user, Event event) {
		return Bookmark.builder()
			.user(user)
			.event(event)
			.build();
	}

	public static EventListResponseDTO toEventListResponseDTO(Bookmark bookmark) {
		Event event = bookmark.getEvent();

		return EventListResponseDTO.builder()
			.id(event.getId())
			.bannerImageUrl(event.getBannerImageUrl())
			.title(event.getTitle())
			.hostChannelName(event.getHostChannel().getName())
			.startDate(DateFormatterUtil.formatDate(event.getStartDate()))
			.address(event.getAddress())
			.onlineType(String.valueOf(event.getOnlineType()))

			.hashtags(event.getHashtags().stream()
				.map(Hashtag::getName)
				.toList()
			)

			.remainDays(DateUtil.getDdayStatus(
				LocalDate.from(event.getStartDate()),
				LocalDate.from(event.getEndDate())))
			.build();
	}
}