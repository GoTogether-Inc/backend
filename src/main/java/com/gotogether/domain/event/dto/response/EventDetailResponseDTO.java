package com.gotogether.domain.event.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventDetailResponseDTO {
	private Long id;
	private String bannerImageUrl;
	private String title;
	private int participantCount;
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;
	private String location;
	private String description;
	private String hostChannelName;
	private String hostChannelDescription;
	private String hostEmail;
	private String hostPhoneNumber;
	private Map<String, String> referenceLinks;
}