package com.gotogether.global.oauth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLogoutService {

	private final TokenBlacklistService tokenBlacklistService;

	public void logout(String accessToken) {
		tokenBlacklistService.blacklistToken(accessToken);
	}
}