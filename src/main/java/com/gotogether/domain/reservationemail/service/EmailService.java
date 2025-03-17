package com.gotogether.domain.reservationemail.service;

public interface EmailService {
	void sendEmail(String[] recipients, String subject, String content);
}