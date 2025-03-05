package com.gotogether.global.constants;

import java.util.List;

public final class Constants {

	private Constants() {}

	public static List<String> NO_NEED_FILTER_URLS = List.of(
		"/oauth2/authorization/kakao",
		"/oauth2/authorization/google",
		"/swagger-ui.html/**",
		"/v3/api-docs/**",
		"/swagger-ui/**"
	);
}