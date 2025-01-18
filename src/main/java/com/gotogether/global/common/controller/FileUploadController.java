package com.gotogether.global.common.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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

	private final FileUploadService awsS3Service;

	@Value("${amazon.aws.bucket}")
	private String bucketName;

	@GetMapping("/api/generate-presigned-url")
	public ApiResponse<?> generatePresignedUrl(@RequestParam String extension) {
		return ApiResponse.onSuccess(
			awsS3Service.generatePreSignUrl(UUID.randomUUID() + "." + extension,
				bucketName, HttpMethod.PUT));
	}

}