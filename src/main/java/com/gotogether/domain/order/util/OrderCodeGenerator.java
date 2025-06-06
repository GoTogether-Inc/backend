package com.gotogether.domain.order.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCodeGenerator {

	private static final String BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int LENGTH = 6;
	private static final SecureRandom random = new SecureRandom();

	public static String generate() {
		StringBuilder sb = new StringBuilder(LENGTH);
		for (int i = 0; i < LENGTH; i++) {
			int index = random.nextInt(BASE36.length());
			sb.append(BASE36.charAt(index));
		}
		return sb.toString();
	}
}