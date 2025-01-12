package com.gotogether.domain.hashtag.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.hashtag.dto.request.HashtagRequestDTO;
import com.gotogether.domain.hashtag.dto.response.HashtagListResponseDTO;
import com.gotogether.domain.hashtag.entity.Hashtag;
import com.gotogether.domain.hashtag.service.HashtagService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hashtags")
public class HashtagController {

	private final HashtagService hashtagService;

	@PostMapping
	public ApiResponse<?> createHashtag(@RequestBody HashtagRequestDTO request) {
		Hashtag hashtag = hashtagService.createHashtag(request);
		return ApiResponse.onSuccessCreated("hashtagId: " + hashtag.getId());
	}

	@GetMapping
	public ApiResponse<?> getHashtags(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<HashtagListResponseDTO> hostChannels = hashtagService.getHashtags(pageable);
		return ApiResponse.onSuccess(hostChannels.getContent());
	}

	@PutMapping("/{hashtagId}")
	public ApiResponse<?> updateHashtag(@PathVariable Long hashtagId,
		@RequestBody HashtagRequestDTO request) {
		Hashtag hashtag = hashtagService.updateHashtag(hashtagId, request);
		return ApiResponse.onSuccess("hashtagId: " + hashtag.getId());
	}

	@DeleteMapping("/{hashtagId}")
	public ApiResponse<?> deleteHashtag(@PathVariable Long hashtagId) {
		hashtagService.deleteHashtag(hashtagId);
		return ApiResponse.onSuccess("해시태그 삭제 성공");
	}
}