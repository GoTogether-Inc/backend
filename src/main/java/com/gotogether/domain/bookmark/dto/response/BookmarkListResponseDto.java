package com.gotogether.domain.bookmark.dto.response;

import com.gotogether.domain.hashtag.entity.Hashtag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookmarkListResponseDto {
    private Long eventId;
    private String eventTitle;
    private String eventBanner;
    private String hostChannelName;
    private String eventStartDate;
    private String eventLocation;
    private List<String> eventHashtags;
}
