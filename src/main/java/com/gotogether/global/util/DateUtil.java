package com.gotogether.global.util;

import java.time.LocalDate;

public class DateUtil {

	private DateUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String getDdayStatus(LocalDate startDate, LocalDate endDate) {
		LocalDate today = LocalDate.now();
		long remainDaysStart = startDate.toEpochDay() - today.toEpochDay();
		long remainDaysEnd = endDate.toEpochDay() - today.toEpochDay();

		if (remainDaysStart > 7) {
			return "false";
		} else if (remainDaysStart > 0) {
			return "D-" + remainDaysStart;
		} else if (remainDaysStart <= 0 && remainDaysEnd >= 0) {
			return "진행중";
		} else {
			return "종료";
		}
	}
}