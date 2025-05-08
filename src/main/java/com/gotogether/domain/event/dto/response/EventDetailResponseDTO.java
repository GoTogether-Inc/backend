package com.gotogether.domain.event.dto.response;

import java.util.List;
import java.util.Map;

import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;

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
	private String address;
	private String detailAddress;
	private Map<String, Double> location;
	private String description;
	private String hostChannelName;
	private String hostChannelDescription;
	private String organizerEmail;
	private String organizerPhoneNumber;
	private List<ReferenceLinkDTO> referenceLinks;
	private String category;
	private String onlineType;
	private String status;
	private List<String> hashtags;
	private Long bookmardId;
	private boolean isBookmarked;
}