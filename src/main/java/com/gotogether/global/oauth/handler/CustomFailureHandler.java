package com.gotogether.global.oauth.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.gotogether.global.apipayload.code.status.ErrorStatus;
import com.gotogether.global.oauth.exception.DuplicatedEmailException;
import com.gotogether.global.oauth.util.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		if (exception instanceof DuplicatedEmailException) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._USER_EMAIL_ALREADY_EXISTS);
		} else {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}