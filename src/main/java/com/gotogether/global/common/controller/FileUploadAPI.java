package com.gotogether.global.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gotogether.global.apipayload.ApiResponse;
import com.gotogether.global.common.dto.S3UrlResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FileUpload", description = "파일 업로드 API")
public interface FileUploadAPI {

	@Operation(
		summary = "S3 Pre-signed URL 생성",
		description = "파일 업로드를 위한 S3 Pre-signed URL을 생성합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "Pre-signed URL 생성 성공",
			content = @Content(schema = @Schema(implementation = S3UrlResponseDTO.class))
		)
	})
	@GetMapping("/generate-presigned-url")
	ApiResponse<S3UrlResponseDTO> generatePresignedUrl(
		@Parameter(description = "업로드할 파일명", required = true) @RequestParam String fileName
	);
} 