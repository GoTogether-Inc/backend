package com.gotogether.domain.event.dto.response;

import java.util.List;

import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponseDTO {
	private Long id;
	private Long hostChannelId;
	private String bannerImageUrl;
	private String title;
	private Long participantCount;
	private String startDate;
	private String endDate;
	private String address;
	private String detailAddress;
	private Double locationLat;
	private Double locationLng;
	private String description;
	private String hostChannelName;
	private String hostChannelDescription;
	private String organizerEmail;
	private String organizerPhoneNumber;
	private List<ReferenceLinkDTO> referenceLinks;
	private List<String> hashtags;
	private String category;
	private String onlineType;
	private String status;
	private Long bookmarkId;
	private boolean bookmarked;
}