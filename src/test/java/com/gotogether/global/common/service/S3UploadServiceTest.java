package com.gotogether.global.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.gotogether.global.common.dto.S3UrlResponseDTO;
import com.gotogether.global.service.MetricService;

@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

	@Mock
	private AmazonS3 amazonS3;

	@Mock
	private MetricService metricService;

	@InjectMocks
	private S3UploadService s3UploadService;

	private final Long userId = 1L;
	private final String bucketName = "test-bucket";
	private final String fileName = "test-image.webp";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(s3UploadService, "bucketName", bucketName);
	}

	@Test
	@DisplayName("S3 Pre-signed URL 생성")
	void generatePreSignUrl() throws Exception {
		// GIVEN
		String expectedUrl = "https://test-bucket.s3.amazonaws.com/temp/1/uuid_test-image.webp?AWSAccessKeyId=test&Expires=1234567890&Signature=test";
		URL mockUrl = new URL(expectedUrl);

		given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
			.willReturn(mockUrl);

		// WHEN
		S3UrlResponseDTO result = s3UploadService.generatePreSignUrl(userId, fileName, HttpMethod.PUT);

		// THEN
		assertThat(result).isNotNull();
		assertThat(result.getPreSignedUrl()).isEqualTo(expectedUrl);

		verify(amazonS3).generatePresignedUrl(argThat(request ->
			request.getBucketName().equals(bucketName) &&
				request.getKey().contains("temp/" + userId + "/") &&
				request.getKey().contains(fileName) &&
				request.getMethod().equals(HttpMethod.PUT) &&
				request.getContentType().equals("image/webp")
		));
		verify(metricService).recordPresignedUrlGeneration(true);
	}

	@Test
	@DisplayName("임시 이미지를 최종 경로로 이동")
	void moveTempImageToFinal() {
		// GIVEN
		String tempImageUrl = "https://test-bucket.s3.amazonaws.com/temp/1/uuid_conference-banner.webp";
		String expectedFinalUrl = "https://test-bucket.s3.amazonaws.com/final/1/uuid_conference-banner.webp";

		// WHEN
		String result = s3UploadService.moveTempImageToFinal(tempImageUrl);

		// THEN
		assertThat(result).isEqualTo(expectedFinalUrl);

		verify(amazonS3).copyObject(argThat(request ->
			request.getSourceBucketName().equals(bucketName) &&
				request.getSourceKey().equals("temp/1/uuid_conference-banner.webp") &&
				request.getDestinationBucketName().equals(bucketName) &&
				request.getDestinationKey().equals("final/1/uuid_conference-banner.webp")
		));
		verify(amazonS3).deleteObject(argThat(request ->
			request.getBucketName().equals(bucketName) &&
				request.getKey().equals("temp/1/uuid_conference-banner.webp")
		));
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// GIVEN
		String imageUrl = "https://test-bucket.s3.amazonaws.com/final/1/uuid_conference-banner.webp";

		// WHEN
		s3UploadService.deleteFile(imageUrl);

		// THEN
		verify(amazonS3).deleteObject(argThat(request ->
			request.getBucketName().equals(bucketName) &&
				request.getKey().equals("final/1/uuid_conference-banner.webp")
		));
	}
} 