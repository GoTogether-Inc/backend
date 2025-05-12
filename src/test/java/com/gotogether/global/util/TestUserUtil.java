package com.gotogether.global.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
		User user = User.builder()
			.name("test User")
			.email("test@example.com")
			.phoneNumber("010-9999-9999")
			.provider("testProvider")
			.providerId("testProviderId")
			.build();

		user = userRepository.save(user);
		TokenDTO tokenDTO = jwtUtil.generateTokens(user.getProviderId());

		Cookie accessTokenCookie = new Cookie("accessToken", tokenDTO.getAccessToken());
		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDTO.getRefreshToken());

		return new TestUser(user, accessTokenCookie, refreshTokenCookie);
	}

	public record TestUser(User user, Cookie accessTokenCookie, Cookie refreshTokenCookie) {
	}
} 