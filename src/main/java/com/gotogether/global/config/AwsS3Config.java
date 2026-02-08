package com.gotogether.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsS3Config {
	@Value("${amazon.aws.accessKey}")
	private String accessKeyId;

	@Value("${amazon.aws.secretKey}")
	private String accessKeySecret;

	@Value("${amazon.aws.region}")
	private String s3RegionName;

	@Value("${amazon.aws.endpoint}")
	private String s3Endpoint;

	@Value("${amazon.aws.path-style-access}")
	private boolean s3PathStyleAccess;

	@Bean
	public AmazonS3 getAmazonS3Client() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);

		return AmazonS3ClientBuilder
			.standard()
			.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Endpoint, s3RegionName))
			.withPathStyleAccessEnabled(s3PathStyleAccess)
			.build();
	}
}