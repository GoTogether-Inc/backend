package com.gotogether.global.common.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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

	public String moveTempImageToFinal(String imageUrl) {
		if (imageUrl == null || !imageUrl.contains("temp/")) {
			return imageUrl;
		}

		String tempKey = extractKeyFromUrl(imageUrl);
		String finalKey = tempKey.replace("temp/", "final/");
		String finalUrl = imageUrl.replace("temp/", "final/");

		moveFile(tempKey, finalKey);
		return finalUrl;
	}

	public void deleteFile(String imageUrl) {
		String key = extractKeyFromUrl(imageUrl);

		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
		amazonS3.deleteObject(deleteObjectRequest);
	}

	private String extractKeyFromUrl(String url) {
		String encodedKey = url.substring(url.indexOf(".com/") + 5);
		return URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
	}

	private void moveFile(String tempKey, String finalKey) {
		CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, tempKey, bucketName, finalKey);
		amazonS3.copyObject(copyObjRequest);

		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, tempKey);
		amazonS3.deleteObject(deleteObjectRequest);
	}
}
