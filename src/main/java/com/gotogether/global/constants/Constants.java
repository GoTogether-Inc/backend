package com.gotogether.global.constants;

import java.util.List;

public final class Constants {

	private Constants() {
	}

	public static List<String> NO_NEED_FILTER_URLS = List.of(
		"/actuator/**",
		"/oauth2/authorization/kakao",
		"/oauth2/authorization/google",
		"/api/v1/events",
		"/api/v1/events/{eventId}",
		"/api/v1/events/categories",
		"/api/v1/host-channels/{hostChannelId}/info",
		"/api/v1/tickets"
	);
}