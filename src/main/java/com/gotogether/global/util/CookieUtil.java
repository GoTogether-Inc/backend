package com.gotogether.global.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

	public static Cookie createCookie(String name, String value, long expiration) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge((int)getTokenMaxAgeInSeconds(expiration));
		return cookie;
	}

	public static long getTokenMaxAgeInSeconds(long expiration) {
		long now = System.currentTimeMillis();
		return (expiration - now) / 1000;
	}
}