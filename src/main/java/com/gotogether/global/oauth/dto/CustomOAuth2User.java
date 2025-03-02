package com.gotogether.global.oauth.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.gotogether.domain.user.dto.request.UserDTO;

public class CustomOAuth2User implements OAuth2User {

	private final UserDTO userDTO;

	public CustomOAuth2User(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

	@Override
	public Map<String, Object> getAttributes() {

		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();

		collection.add(() -> "ROLE_USER");

		return collection;
	}

	public String getName() {

		return userDTO.getName();
	}

	public String getProviderId() {
		return userDTO.getProviderId();
	}

	public String getProvider() {
		return userDTO.getProvider();
	}

	public String getEmail() {
		return userDTO.getEmail();
	}
}
