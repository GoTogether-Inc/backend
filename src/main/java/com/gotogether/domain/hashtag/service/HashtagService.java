package com.gotogether.domain.hashtag.service;

import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;

public interface HashtagService {
	Hashtag createHashtag(HashtagRequestDTO request);
}