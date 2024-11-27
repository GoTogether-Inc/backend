package com.gotogether.domain.event.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.gotogether.domain.alert.entity.Alert;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hostchannel.entity.HostChannel;
import com.gotogether.domain.referencelink.entity.ReferenceLink;
import com.gotogether.domain.ticket.entity.Ticket;
import com.gotogether.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event")
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_channel_id", nullable = false)
	private HostChannel hostChannel;

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

	@Column(name = "host_email", nullable = false)
	private String hostEmail;

	@Column(name = "host_phone_number", nullable = false)
	private String hostPhoneNumber;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Ticket> tickets;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Hashtag> hashtags;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Alert> alerts;

	@OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ReferenceLink> referenceLinks;

	@Builder
	public Event(HostChannel hostChannel, String title, String description, LocalDateTime startDate,
		LocalDateTime endDate, String bannerImageUrl, String location, OnlineType onlineType, Category category,
		String hostEmail, String hostPhoneNumber) {
		this.hostChannel = hostChannel;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.bannerImageUrl = bannerImageUrl;
		this.location = location;
		this.onlineType = onlineType;
		this.category = category;
		this.hostEmail = hostEmail;
		this.hostPhoneNumber = hostPhoneNumber;
	}
}