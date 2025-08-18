package com.gotogether.global.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.oauth.dto.TokenDTO;
import com.gotogether.global.oauth.util.JWTUtil;

import jakarta.servlet.http.Cookie;

@Component
public class TestUserUtil {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JWTUtil jwtUtil;

	public TestUser createTestUser() {
		User user = userRepository.findByProviderId("TestProviderId")
			.orElseGet(() -> userRepository.save(User.builder()
				.name("Test User")
				.email("test@example.com")
				.phoneNumber("010-9999-9999")
				.provider("TestProvider")
				.providerId("TestProviderId")
				.build()));

		user = userRepository.save(user);
		
		ReflectionTestUtils.setField(user, "id", 1L);

		TokenDTO tokenDTO = jwtUtil.generateTokens(user.getProviderId());

		Cookie accessTokenCookie = new Cookie("accessToken", tokenDTO.getAccessToken());
		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDTO.getRefreshToken());

		return new TestUser(user, accessTokenCookie, refreshTokenCookie);
	}

	public record TestUser(User user, Cookie accessTokenCookie, Cookie refreshTokenCookie) {
	}
} 