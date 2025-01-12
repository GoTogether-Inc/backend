package com.gotogether.domain.event.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventRequestDTO {
	@JsonProperty("hostChannelId")
	private Long hostChannelId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("startDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate startDate;

	@JsonProperty("endDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endDate;

	@JsonProperty("startTime")
	private String startTime;

	@JsonProperty("endTime")
	private String endTime;

	@JsonProperty("bannerImageUrl")
	private String bannerImageUrl;

	@JsonProperty("description")
	private String description;

	@JsonProperty("referenceLinks")
	private List<ReferenceLinkDTO> referenceLinks;

	@JsonProperty("onlineType")
	private OnlineType onlineType;

	@JsonProperty("location")
	private String location;

	@JsonProperty("category")
	private Category category;

	@JsonProperty("hashtags")
	private List<String> hashtags;

	@JsonProperty("organizerEmail")
	private String organizerEmail;

	@JsonProperty("organizerPhoneNumber")
	private String organizerPhoneNumber;
}