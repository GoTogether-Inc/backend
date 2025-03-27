package com.gotogether.domain.hashtag.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.event.entity.Event;
import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.dto.response.HashtagListResponseDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;

public interface HashtagService {
	Hashtag createHashtag(HashtagRequestDTO request);

	Page<HashtagListResponseDTO> getHashtags(Pageable pageable);

	Hashtag updateHashtag(Long hashtagId, HashtagRequestDTO request);

	void deleteHashtag(Long hashtagId);

	void createHashtags(Event event, List<String> hashtags);

	void deleteHashtagsByRequest(Event event, List<String> hashtags);

	void deleteHashtagsByEvent(Event event);
}