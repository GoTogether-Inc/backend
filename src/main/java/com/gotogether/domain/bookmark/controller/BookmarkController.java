package com.gotogether.domain.bookmark.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.service.BookmarkService;
import com.gotogether.domain.event.dto.response.EventListResponseDTO;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/{eventId}/bookmark")
public class BookmarkController {

	private final BookmarkService bookmarkService;

	@PostMapping
	public ApiResponse<?> createBookmark(@PathVariable Long eventId, @AuthUser Long userId) {
		Bookmark bookmark = bookmarkService.createBookmark(eventId, userId);
		return ApiResponse.onSuccessCreated(bookmark.getId());
	}

	@GetMapping
	public ApiResponse<?> getUserBookmarks(@AuthUser Long userId) {
		List<EventListResponseDTO> bookmarkList = bookmarkService.getUserBookmarks(userId);
		return ApiResponse.onSuccess(bookmarkList);
	}

	@DeleteMapping("/{bookmarkId}")
	public ApiResponse<?> deleteBookmark(@PathVariable Long bookmarkId) {
		bookmarkService.deleteBookmark(bookmarkId);
		return ApiResponse.onSuccess("북마크 삭제 성공");
	}
}