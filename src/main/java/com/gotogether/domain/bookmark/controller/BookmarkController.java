package com.gotogether.domain.bookmark.controller;

import com.gotogether.domain.bookmark.dto.response.BookmarkListResponseDTO;
import org.springframework.web.bind.annotation.*;

import com.gotogether.domain.bookmark.entity.Bookmark;
import com.gotogether.domain.bookmark.service.BookmarkService;
import com.gotogether.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/{eventId}/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ApiResponse<?> createBookmark(@PathVariable Long eventId, @RequestParam Long userId) {
        Bookmark bookmark = bookmarkService.createBookmark(eventId, userId);
        return ApiResponse.onSuccessCreated("bookmarkId: " + bookmark.getId());
    }

    @GetMapping
    public ApiResponse<?> getUserBookmarks(@RequestParam Long userId) {
        List<BookmarkListResponseDTO> bookmarkList = bookmarkService.getUserBookmarks(userId);
        return ApiResponse.onSuccess(bookmarkList);
    }

    @DeleteMapping("{/bookmarkId}")
    public ApiResponse<?> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ApiResponse.onSuccess("북마크 삭제 성공");
    }
}