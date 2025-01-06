package com.gotogether.domain.hashtag.converter;

import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;

public class HashtagConverter {

	public static Hashtag of(HashtagRequestDTO request) {
		return Hashtag.builder()
			.name(request.getHashtagName())
			.build();
	}
}