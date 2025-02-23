package com.gotogether.domain.oauth.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {

	private SecretKey secretKey;

	public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public String getProviderId(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("providerId", String.class);
	}

	public String getRole(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("role", String.class);
	}

	public String getTokenType(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("tokenType", String.class);
	}

	public Boolean isExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public String createJwt(String providerId, String role, String tokenType, Long expiredMs) {

		return Jwts.builder()
			.claim("providerId", providerId)
			.claim("role", role)
			.claim("tokenType", tokenType)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs * 1000))
			.signWith(secretKey)
			.compact();
	}

	public boolean validateAuthorizationHeader(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return false;
		}

		String token = extractTokenFromAuthorizationHeader(authorizationHeader);
		return !isExpired(token);
	}

	public String extractTokenFromAuthorizationHeader(String authorizationHeader) {
		return authorizationHeader.replace("Bearer ", "").trim();
	}
}