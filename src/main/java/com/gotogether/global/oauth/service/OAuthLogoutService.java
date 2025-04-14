package com.gotogether.global.oauth.service;

import static com.gotogether.global.util.CookieUtil.*;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLogoutService {

	private final TokenBlacklistService tokenBlacklistService;

	public void logout(String accessToken, HttpServletResponse response) {
		tokenBlacklistService.blacklistToken(accessToken);

		deleteCookie("accessToken", response);
		deleteCookie("refreshToken", response);
	}
}