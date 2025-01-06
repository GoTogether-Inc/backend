package com.gotogether.domain.hashtag.entity;

import java.util.List;

import com.gotogether.domain.eventhashtag.entity.EventHashtag;
import com.gotogether.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hashtag")
public class Hashtag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "hashtag", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<EventHashtag> eventHashtags;

	@Builder
	public Hashtag(String name) {
		this.name = name;
	}

	public void update(String hashtagName) {
		this.name = hashtagName;
	}
}
