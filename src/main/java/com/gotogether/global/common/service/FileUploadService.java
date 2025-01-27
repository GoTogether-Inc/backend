package com.gotogether.global.common.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {

	@Value("${amazon.aws.bucket}")
	private String bucketName;

	private final AmazonS3 amazonS3;

	public String generatePreSignUrl(String filePath,
		HttpMethod httpMethod) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 10);
		return amazonS3.generatePresignedUrl(bucketName, filePath, calendar.getTime(), httpMethod).toString();

	}
}
