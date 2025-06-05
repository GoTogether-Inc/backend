package com.gotogether.domain.order.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCodeGenerator {

	private final RedisTemplate<String, String> redisTemplate;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

	public String generateTodayOrderCode() {
		String today = LocalDate.now().format(DATE_FORMATTER);
		String redisKey = "order:seq:" + today;

		Long sequence = redisTemplate.opsForValue().increment(redisKey);
		redisTemplate.expire(redisKey, Duration.ofDays(1));

		String paddedSeq = String.format("%06d", sequence);
		return today + paddedSeq;
	}
}