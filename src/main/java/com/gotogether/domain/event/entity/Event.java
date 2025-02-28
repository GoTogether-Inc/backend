package com.gotogether.domain.event.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.event.dto.request.EventRequestDTO;
import com.gotogether.domain.eventhashtag.entity.EventHashtag;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.entity.ReferenceLink;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "banner_image_url", nullable = false)
	private String bannerImageUrl;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "online_type", nullable = false)
	private OnlineType onlineType;

	@Column(name = "category", nullable = false)
	private Category category;

	@Column(name = "organizer_email", nullable = false)
	private String organizerEmail;

	@Column(name = "organizer_phone_number", nullable = false)
	private String organizerPhoneNumber;

	@Column(name = "status", nullable = false)
	private EventStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_channel_id", nullable = false)
	private HostChannel hostChannel;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Ticket> tickets;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<EventHashtag> eventHashtags;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ReservationEmail> reservationEmails;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ReferenceLink> referenceLinks;

	@Builder
	public Event(String title, String description, LocalDateTime startDate,
		LocalDateTime endDate, String bannerImageUrl, String location, OnlineType onlineType, Category category,
		String organizerEmail, String organizerPhoneNumber, HostChannel hostChannel) {
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.bannerImageUrl = bannerImageUrl;
		this.location = location;
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
		this.startDate = request.getStartDate().atTime(LocalTime.parse(request.getStartTime()));
		this.endDate = request.getEndDate().atTime(LocalTime.parse(request.getEndTime()));
		this.bannerImageUrl = request.getBannerImageUrl();
		this.location = request.getLocation();
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
}
