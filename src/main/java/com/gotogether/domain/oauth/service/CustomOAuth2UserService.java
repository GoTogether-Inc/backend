package com.gotogether.domain.oauth.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.gotogether.domain.oauth.dto.CustomOAuth2User;
import com.gotogether.domain.oauth.dto.GoogleResponseDTO;
import com.gotogether.domain.oauth.dto.KakaoResponseDTO;
import com.gotogether.domain.oauth.dto.OAuth2Response;
import com.gotogether.domain.user.dto.request.UserDTO;
import com.gotogether.domain.user.entity.User;
import com.gotogether.domain.user.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	public CustomOAuth2UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
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
				.phoneNumber("-")
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			userRepository.save(userEntity);

			UserDTO userDTO = UserDTO.builder()
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
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.provider(oAuth2Response.getProvider())
				.providerId(oAuth2Response.getProviderId())
				.build();

			return new CustomOAuth2User(userDTO);
		}
	}
}