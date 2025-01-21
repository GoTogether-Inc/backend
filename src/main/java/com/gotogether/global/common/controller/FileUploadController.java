package com.gotogether.global.common.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.common.service.FileUploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

	private final FileUploadService fileUploadService;

	@GetMapping("/api/generate-presigned-url")
	public ApiResponse<?> generatePresignedUrl(@RequestParam String fileName) {
		return ApiResponse.onSuccess(
			fileUploadService.generatePreSignUrl(UUID.randomUUID() + fileName, HttpMethod.PUT));
	}
}