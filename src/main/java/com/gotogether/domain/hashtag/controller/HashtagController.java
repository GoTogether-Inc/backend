package com.gotogether.domain.hashtag.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.service.HashtagService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hashtags")
public class HashtagController {

	private final HashtagService hashtagService;

	@PostMapping
	public ApiResponse<?> createHashtag(@RequestBody HashtagRequestDTO request) {
		log.info("Request payload: {}", request);
		Hashtag hashtag = hashtagService.createHashtag(request);
		return ApiResponse.onSuccessCreated("hashtagId: " + hashtag.getId());
	}
}