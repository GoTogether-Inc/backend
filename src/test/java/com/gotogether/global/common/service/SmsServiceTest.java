package com.gotogether.global.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.common.dto.SmsRequestDTO;
import com.gotogether.global.common.dto.SmsVerifyRequestDTO;
import com.gotogether.global.service.MetricService;
import com.gotogether.global.util.SmsCertificationUtil;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

	@Mock
	private SmsCertificationUtil smsCertificationUtil;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private MetricService metricService;

	@InjectMocks
	private SmsService smsService;

	private SmsRequestDTO smsRequestDTO;
	private SmsVerifyRequestDTO smsVerifyRequestDTO;

	@BeforeEach
	void setUp() {
		smsRequestDTO = SmsRequestDTO.builder()
			.phoneNumber("010-1234-5678")
			.build();

		smsVerifyRequestDTO = SmsVerifyRequestDTO.builder()
			.phoneNumber("010-1234-5678")
			.certificationCode("123456")
			.build();
	}

	@Test
	@DisplayName("SMS 인증번호 발송")
	void sendCertificationCode() {
		// GIVEN
		String phoneNumber = "01012345678";
		String key = "SMS:CERT:" + phoneNumber;

		given(redisTemplate.hasKey(key)).willReturn(false);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		// WHEN
		smsService.sendCertificationCode(smsRequestDTO);

		// THEN
		verify(redisTemplate).hasKey(key);
		verify(valueOperations).set(eq(key), anyString(), eq(Duration.ofMinutes(3)));
		verify(smsCertificationUtil).sendSMS(eq("010-1234-5678"), anyString());
		verify(metricService).recordSmsDispatch(true);
	}

	@Test
	@DisplayName("SMS 인증번호 발송 실패 - 이미 인증번호 존재")
	void sendCertificationCode_AlreadyExists() {
		// GIVEN
		String phoneNumber = "01012345678";
		String key = "SMS:CERT:" + phoneNumber;

		given(redisTemplate.hasKey(key)).willReturn(true);

		// WHEN & THEN
		assertThatThrownBy(() -> smsService.sendCertificationCode(smsRequestDTO))
			.isInstanceOf(GeneralException.class)
			.extracting("code")
			.isEqualTo(ErrorStatus._SMS_ALREADY_SEND);

		verify(redisTemplate).hasKey(key);
	}

	@Test
	@DisplayName("SMS 인증번호 검증")
	void verifyCertificationCode() {
		// GIVEN
		String phoneNumber = "01012345678";
		String key = "SMS:CERT:" + phoneNumber;
		String expectedCode = "123456";

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.get(key)).willReturn(expectedCode);

		// WHEN
		smsService.verifyCertificationCode(smsVerifyRequestDTO);

		// THEN
		verify(valueOperations).get(key);
		verify(redisTemplate).delete(key);
	}

	@Test
	@DisplayName("SMS 인증번호 검증 실패 - 인증번호 만료")
	void verifyCertificationCode_Expired() {
		// GIVEN
		String phoneNumber = "01012345678";
		String key = "SMS:CERT:" + phoneNumber;

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.get(key)).willReturn(null);

		// WHEN & THEN
		assertThatThrownBy(() -> smsService.verifyCertificationCode(smsVerifyRequestDTO))
			.isInstanceOf(GeneralException.class)
			.extracting("code")
			.isEqualTo(ErrorStatus._SMS_CERTIFICATION_EXPIRED);

		verify(valueOperations).get(key);
	}

	@Test
	@DisplayName("SMS 인증번호 검증 실패 - 인증번호 불일치")
	void verifyCertificationCode_Mismatch() {
		// GIVEN
		String phoneNumber = "01012345678";
		String key = "SMS:CERT:" + phoneNumber;
		String expectedCode = "654321";

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.get(key)).willReturn(expectedCode);

		// WHEN & THEN
		assertThatThrownBy(() -> smsService.verifyCertificationCode(smsVerifyRequestDTO))
			.isInstanceOf(GeneralException.class)
			.extracting("code")
			.isEqualTo(ErrorStatus._SMS_CERTIFICATION_MISMATCH);

		verify(valueOperations).get(key);
	}
} 