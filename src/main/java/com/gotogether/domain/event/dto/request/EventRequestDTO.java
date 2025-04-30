package com.gotogether.domain.event.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.gotogether.domain.event.entity.Category;
import com.gotogether.domain.event.entity.OnlineType;
import com.gotogether.domain.referencelink.dto.ReferenceLinkDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventRequestDTO {

	@NotNull(message = "hostChannelId는 필수입니다.")
	private Long hostChannelId;

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotNull(message = "시작 일시는 필수입니다.")
	private LocalDateTime startDate;

	@NotNull(message = "종료 일시는 필수입니다.")
	@Future(message = "종료 일시는 미래여야 합니다.")
	private LocalDateTime endDate;

	@NotBlank(message = "배너 이미지 URL은 필수입니다.")
	private String bannerImageUrl;

	@NotBlank(message = "설명은 필수입니다.")
	private String description;

	@Valid
	private List<ReferenceLinkDTO> referenceLinks;

	@NotNull(message = "온라인 타입은 필수입니다.")
	private OnlineType onlineType;
	
	private String address;

	@NotNull(message = "위도는 필수입니다.")
	private Double locationLat;

	@NotNull(message = "경도는 필수입니다.")
	private Double locationLng;

	@NotNull(message = "카테고리는 필수입니다.")
	private Category category;

	@Size(max = 5, message = "해시태그는 최대 5개까지 가능합니다.")
	private List<String> hashtags;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	private String organizerEmail;

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다.")
	private String organizerPhoneNumber;
}