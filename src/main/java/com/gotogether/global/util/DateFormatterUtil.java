package com.gotogether.global.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	private DateFormatterUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String formatDate(LocalDateTime dateTime) {
		return dateTime.toLocalDate().format(DATE_FORMATTER);
	}

	public static String formatTime(LocalTime time) {
		return time.format(TIME_FORMATTER);
	}
}