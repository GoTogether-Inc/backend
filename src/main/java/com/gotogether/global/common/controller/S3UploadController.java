package com.gotogether.global.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.gotogether.global.annotation.AuthUser;
import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.common.dto.S3UrlResponseDTO;
import com.gotogether.global.common.service.S3UploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class S3UploadController implements S3UploadApi {

	private final S3UploadService s3UploadService;

	@GetMapping("/generate-presigned-url")
	public ApiResponse<S3UrlResponseDTO> generatePresignedUrl(
		@AuthUser Long userId,
		@RequestParam String fileName) {
		return ApiResponse.onSuccess(
			s3UploadService.generatePreSignUrl(userId, fileName, HttpMethod.PUT));
	}
}