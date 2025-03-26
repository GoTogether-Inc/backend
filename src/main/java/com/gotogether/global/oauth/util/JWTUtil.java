package com.gotogether.global.oauth.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gotogether.global.oauth.dto.TokenDTO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JWTUtil {

	private final SecretKey secretKey;
	private final long accessExpiration;
	private final long refreshExpiration;

	public JWTUtil(
		@Value("${spring.jwt.secret}") String secret,
		@Value("${spring.jwt.access-token-expiration}") long accessExpiration,
		@Value("${spring.jwt.refresh-token-expiration}") long refreshExpiration
	) {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.accessExpiration = accessExpiration;
		this.refreshExpiration = refreshExpiration;
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
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		} catch (MalformedJwtException e) {
			System.out.println("Malformed JWT: " + e.getMessage());
			return true;
		} catch (SignatureException e) {
			System.out.println("Invalid signature: " + e.getMessage());
			return true;
		} catch (JwtException e) {
			System.out.println("JWT error: " + e.getMessage());
			return true;
		} catch (Exception e) {
			System.out.println("Unexpected error: " + e.getMessage());
			return true;
		}
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

	public TokenDTO generateTokens(String providerId) {
		return TokenDTO.of(
			createJwt(providerId, "ROLE_USER", "access", accessExpiration),
			createJwt(providerId, "ROLE_USER", "refresh", refreshExpiration)
		);
	}
}