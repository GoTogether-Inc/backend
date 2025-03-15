package com.gotogether.domain.bookmark.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkListResponseDTO {
	private Long eventId;
	private String eventTitle;
	private String eventBanner;
	private String hostChannelName;
	private String eventStartDate;
	private String eventAddress;
	private List<String> eventHashtags;
}
