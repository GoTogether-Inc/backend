package com.gotogether.global.oauth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.gotogether.global.oauth.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

	private final RedisTemplate<String, String> redisTemplate;
	private final JWTUtil jwtUtil;

	public void blacklistToken(String token) {
		long ttl = jwtUtil.getRemainingTime(token);
		if (ttl > 0) {
			redisTemplate.opsForValue().set("blacklist:" + token, "logout", ttl, TimeUnit.SECONDS);
		}
	}

	public boolean isTokenBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
	}
}