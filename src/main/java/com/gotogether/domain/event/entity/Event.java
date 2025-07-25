package com.gotogether.domain.event.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.eventhashtag.entity.EventHashtag;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.entity.ReferenceLink;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "events")
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Lob
	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "banner_image_url", length = 512 , nullable = false)
	private String bannerImageUrl;

	@Column(name = "address")
	private String address;

	@Column(name = "detail_address")
	private String detailAddress;

	@Column(name = "location_lat", nullable = false)
	private Double locationLat;

	@Column(name = "location_lng", nullable = false)
	private Double locationLng;

	@Enumerated(EnumType.STRING)
	@Column(name = "online_type", nullable = false)
	private OnlineType onlineType;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Column(name = "organizer_email", nullable = false)
	private String organizerEmail;

	@Column(name = "organizer_phone_number", nullable = false)
	private String organizerPhoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EventStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_channel_id", nullable = false)
	private HostChannel hostChannel;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Ticket> tickets = new ArrayList<>();

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<EventHashtag> eventHashtags = new ArrayList<>();

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ReservationEmail> reservationEmails = new ArrayList<>();

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ReferenceLink> referenceLinks = new ArrayList<>();

	@Builder
	public Event(String title, String description, LocalDateTime startDate, LocalDateTime endDate,
		String bannerImageUrl, String address, String detailAddress, Double locationLat, Double locationLng,
		OnlineType onlineType, Category category, String organizerEmail, String organizerPhoneNumber,
		HostChannel hostChannel) {
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.bannerImageUrl = bannerImageUrl;
		this.address = address;
		this.detailAddress = detailAddress;
		this.locationLat = locationLat;
		this.locationLng = locationLng;
		this.onlineType = onlineType;
		this.category = category;
		this.organizerEmail = organizerEmail;
		this.organizerPhoneNumber = organizerPhoneNumber;
		this.hostChannel = hostChannel;
		this.status = EventStatus.PROGRESS;
	}

	public void update(EventRequestDTO request) {
		this.title = request.getTitle();
		this.description = request.getDescription();
		this.startDate = request.getStartDate();
		this.endDate = request.getEndDate();
		this.bannerImageUrl = request.getBannerImageUrl();
		this.address = request.getAddress();
		this.detailAddress = request.getDetailAddress();
		this.locationLat = request.getLocationLat();
		this.locationLng = request.getLocationLng();
		this.onlineType = request.getOnlineType();
		this.category = request.getCategory();
		this.organizerEmail = request.getOrganizerEmail();
		this.organizerPhoneNumber = request.getOrganizerPhoneNumber();
	}

	public List<Hashtag> getHashtags() {
		return this.eventHashtags.stream()
			.map(EventHashtag::getHashtag)
			.collect(Collectors.toList());
	}

	public void updateStatus(EventStatus status) {
		this.status = status;
	}

	public void updateBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}
}
