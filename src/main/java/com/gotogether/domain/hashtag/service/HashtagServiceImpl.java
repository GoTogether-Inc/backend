package com.gotogether.domain.hashtag.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.eventhashtag.entity.EventHashtag;
import com.gotogether.domain.eventhashtag.repository.EventHashtagRepository;
import com.gotogether.domain.hashtag.converter.HashtagConverter;
import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.dto.response.HashtagListResponseDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.repository.HashtagRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepository;
	private final EventHashtagRepository eventHashtagRepository;

	@Override
	@Transactional
	public Hashtag createHashtag(HashtagRequestDTO request) {

		if (hashtagRepository.findByName(request.getHashtagName()).isPresent()) {
			throw new GeneralException(ErrorStatus._HASHTAG_EXIST);
		}

		Hashtag hashtag = HashtagConverter.of(request);
		return hashtagRepository.save(hashtag);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<HashtagListResponseDTO> getHashtags(Pageable pageable) {
		Page<Hashtag> hashtags = hashtagRepository.findAll(pageable);

		return hashtags.map(HashtagConverter::toHostChannelListResponseDTO);
	}

	@Override
	public Hashtag updateHashtag(Long hashtagId, HashtagRequestDTO request) {
		Hashtag hashtag = hashtagRepository.findById(hashtagId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HASHTAG_NOT_FOUND));

		hashtag.update(request.getHashtagName());

		hashtagRepository.save(hashtag);
		return hashtag;
	}

	@Override
	@Transactional
	public void deleteHashtag(Long hashtagId) {
		Hashtag hashtag = hashtagRepository.findById(hashtagId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._HASHTAG_NOT_FOUND));

		hashtagRepository.delete(hashtag);
	}

	@Override
	@Transactional
	public void createHashtags(Event event, List<String> hashtags) {
		List<EventHashtag> eventHashtags = hashtags.stream()
			.map(this::normalizeHashtag)
			.map(hashtag -> createOrGetHashtag(hashtag, event))
			.toList();

		eventHashtagRepository.saveAll(eventHashtags);
	}

	@Override
	@Transactional
	public void deleteHashtagsByRequest(Event event, List<String> hashtags) {
		List<Hashtag> existingHashtags = eventHashtagRepository.findHashtagsByEvent(event);

		List<Hashtag> unUsedHashtags = existingHashtags.stream()
			.filter(existingHashtag -> hashtags.stream()
				.noneMatch(hashtag -> normalizeHashtag(hashtag).equals(existingHashtag.getName())))
			.toList();

		for (Hashtag hashtag : unUsedHashtags) {
			if (eventHashtagRepository.countByHashtag(hashtag) == 1) {
				hashtagRepository.delete(hashtag);
			}
		}

		eventHashtagRepository.deleteByEvent(event);
	}

	@Override
	@Transactional
	public void deleteHashtagsByEvent(Event event) {
		List<Hashtag> existingHashtags = eventHashtagRepository.findHashtagsByEvent(event);

		eventHashtagRepository.deleteByEvent(event);

		deleteUnusedHashtags(existingHashtags);
	}

	private EventHashtag createOrGetHashtag(String hashtag, Event event) {
		Hashtag existingHashtag = hashtagRepository.findByName(hashtag)
			.orElseGet(() -> saveNewHashtag(hashtag));

		return EventHashtag.builder()
			.event(event)
			.hashtag(existingHashtag)
			.build();
	}

	private Hashtag saveNewHashtag(String hashtag) {
		return hashtagRepository.save(
			Hashtag.builder()
				.name(hashtag)
				.build()
		);
	}

	private void deleteUnusedHashtags(List<Hashtag> hashtags) {
		for (Hashtag hashtag : hashtags) {
			if (eventHashtagRepository.countByHashtag(hashtag) == 0) {
				hashtagRepository.delete(hashtag);
			}
		}
	}

	private String normalizeHashtag(String hashtag) {
		if (hashtag == null) {
			return null;
		}

		return hashtag.replaceAll("[^a-zA-Z0-9가-힣]", "").toLowerCase();
	}
}