package com.gotogether.global.oauth.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gotogether.domain.user.dto.request.UserDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.apipayload.exception.GeneralException;
import com.gotogether.global.oauth.dto.CustomOAuth2User;
import com.gotogether.global.oauth.dto.GoogleResponseDTO;
import com.gotogether.global.oauth.dto.KakaoResponseDTO;
import com.gotogether.global.oauth.dto.OAuth2Response;
import com.gotogether.global.oauth.dto.TokenDTO;
import com.gotogether.global.oauth.exception.DuplicatedEmailException;
import com.gotogether.global.oauth.util.JWTUtil;
import com.gotogether.global.util.CookieUtil;
import com.gotogether.global.service.MetricService;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;
	private final MetricService metricService;

	public CustomOAuth2UserService(UserRepository userRepository, JWTUtil jwtUtil, MetricService metricService) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
		this.metricService = metricService;
	}

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		logger.info("OAuth 사용자 로드 시작");
		logger.info("Registration ID: {}", userRequest.getClientRegistration().getRegistrationId());

		OAuth2User oAuth2User = super.loadUser(userRequest);
		System.out.println(oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;

		if (registrationId.equals("google")) {
			logger.info("Google OAuth 사용자 처리");
			oAuth2Response = new GoogleResponseDTO(oAuth2User.getAttributes());
		} else if (registrationId.equals("kakao")) {
			logger.info("Kakao OAuth 사용자 처리");
			oAuth2Response = new KakaoResponseDTO(oAuth2User.getAttributes());
		} else {
			logger.warn("알 수 없는 Registration ID: {}", registrationId);
			return null;
		}

		String providerId = oAuth2Response.getProviderId();
		logger.info("Provider ID: {}, Email: {}", providerId, oAuth2Response.getEmail());

		Optional<User> existData = userRepository.findByProviderId(providerId);

		if (existData.isEmpty()) {
			logger.info("신규 사용자 등록: {}", oAuth2Response.getEmail());

			Optional<User> duplicateEmail = userRepository.findByEmail(oAuth2Response.getEmail());
			if (duplicateEmail.isPresent()) {
				logger.warn("중복 이메일 발견: {}", oAuth2Response.getEmail());
				throw new DuplicatedEmailException("이미 가입된 이메일입니다.");
			}

			User userEntity = User.builder()
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			userRepository.save(userEntity);
			logger.info("새 사용자 생성 완료, ID: {}", userEntity.getId());

			metricService.recordUserRegistration(oAuth2Response.getProvider());

			UserDTO userDTO = UserDTO.builder()
				.id(userEntity.getId())
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			return new CustomOAuth2User(userDTO);
		} else {
			logger.info("기존 사용자 로그인: {}", oAuth2Response.getEmail());

			User user = existData.get();
			user.updateEmail(oAuth2Response.getEmail());
			user.updateName(oAuth2Response.getName());

			userRepository.save(user);

			UserDTO userDTO = UserDTO.builder()
				.id(user.getId())
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			return new CustomOAuth2User(userDTO);
		}
	}

	@Transactional
	public TokenDTO reissue(Long userId, HttpServletResponse response) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		TokenDTO tokenDTO = jwtUtil.generateTokens(user.getProviderId());
		long expiration = jwtUtil.getExpiration(tokenDTO.getRefreshToken()).getTime();

		response.addCookie(CookieUtil.createCookie("accessToken", tokenDTO.getAccessToken(), expiration));

		return tokenDTO;
	}
}