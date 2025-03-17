package com.gotogether.domain.reservationemail.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;

	@Override
	public void sendEmail(String[] recipients, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(recipients);
		message.setSubject(subject);
		message.setText(content);
		mailSender.send(message);
	}
}