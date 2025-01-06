package com.gotogether.domain.hashtag.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.hashtag.converter.HashtagConverter;
import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.repository.HashtagRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepository;

	@Override
	@Transactional
	public Hashtag createHashtag(HashtagRequestDTO request) {

		if (hashtagRepository.findByName(request.getHashtagName()).isPresent()) {
			throw new GeneralException(ErrorStatus._HASHTAG_EXIST);
		}

		Hashtag hashtag = HashtagConverter.of(request);
		return hashtagRepository.save(hashtag);

	}
}