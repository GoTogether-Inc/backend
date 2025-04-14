package com.gotogether.global.oauth.service;

import java.util.Optional;

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
import com.gotogether.global.oauth.util.JWTUtil;
import com.gotogether.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;

	public CustomOAuth2UserService(UserRepository userRepository, JWTUtil jwtUtil) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		System.out.println(oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;

		if (registrationId.equals("google")) {

			oAuth2Response = new GoogleResponseDTO(oAuth2User.getAttributes());
		} else if (registrationId.equals("kakao")) {

			oAuth2Response = new KakaoResponseDTO(oAuth2User.getAttributes());
		} else {

			return null;
		}

		String providerId = oAuth2Response.getProviderId();

		Optional<User> existData = userRepository.findByProviderId(providerId);

		if (existData.isEmpty()) {

			User userEntity = User.builder()
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			userRepository.save(userEntity);

			UserDTO userDTO = UserDTO.builder()
				.id(userEntity.getId())
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			return new CustomOAuth2User(userDTO);
		} else {

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