package com.gotogether.global.common.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.common.dto.S3UrlResponseDTO;
import com.gotogether.global.common.service.FileUploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FileUploadController {

	private final FileUploadService fileUploadService;

	@GetMapping("/generate-presigned-url")
	public ApiResponse<S3UrlResponseDTO> generatePresignedUrl(@RequestParam String fileName) {
		return ApiResponse.onSuccess(
			fileUploadService.generatePreSignUrl(UUID.randomUUID() + fileName, HttpMethod.PUT));
	}
}