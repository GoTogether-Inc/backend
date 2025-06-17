package com.gotogether.global.common.service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.gotogether.global.common.dto.S3UrlResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3UploadService {

	@Value("${amazon.aws.bucket}")
	private String bucketName;

	private final AmazonS3 amazonS3;

	public S3UrlResponseDTO generatePreSignUrl(Long userId, String fileName, HttpMethod httpMethod) {
		String filePath = "temp/" + userId + "/" + UUID.randomUUID() + "_" + fileName;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 10);

		String url = amazonS3.generatePresignedUrl(bucketName, filePath, calendar.getTime(), httpMethod).toString();

		return S3UrlResponseDTO.builder()
			.preSignedUrl(url)
			.build();
	}
}
