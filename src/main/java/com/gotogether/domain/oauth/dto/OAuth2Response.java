package com.gotogether.domain.oauth.dto;

public interface OAuth2Response {

	String getProvider();

	String getProviderId();

	String getEmail();

	String getName();
}