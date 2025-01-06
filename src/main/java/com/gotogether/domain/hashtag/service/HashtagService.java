package com.gotogether.domain.hashtag.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.dto.response.HashtagListResponseDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;

public interface HashtagService {
	Hashtag createHashtag(HashtagRequestDTO request);

	Page<HashtagListResponseDTO> getHashtags(Pageable pageable);
}