package com.gotogether.global.common.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.gotogether.global.common.dto.S3UrlResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {

	@Value("${amazon.aws.bucket}")
	private String bucketName;

	private final AmazonS3 amazonS3;

	public S3UrlResponseDTO generatePreSignUrl(String filePath,
		HttpMethod httpMethod) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 10);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filePath)
			.withMethod(httpMethod)
			.withExpiration(calendar.getTime())
			.withContentType("image/webp");

		String url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();

		return S3UrlResponseDTO.builder()
			.preSignedUrl(url)
			.build();
	}
}
